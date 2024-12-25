package com.meshhdawi.productlib.orders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrdersController(
    private val orderService: OrderService
) {

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<OrderEntity> {
        val order = orderService.getOrderById(id)
        return ResponseEntity.ok(order)
    }

    @GetMapping("/user/{customerId}")
    fun getOrdersByCustomer(@PathVariable customerId: Long): ResponseEntity<List<OrderEntity>> {
        val orders = orderService.getOrdersByCustomer(customerId)
        return ResponseEntity.ok(orders)
    }

    @PostMapping
    fun createOrder(@RequestBody orderRequest: OrderRequest): ResponseEntity<OrderEntity> {
        val createdOrder = orderService.createOrder(orderRequest)
        return ResponseEntity.ok(createdOrder)
    }
}
