package life.majd.productlib.orders

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrdersController(
    private val orderService: OrderService
) {

    @GetMapping
    fun getAllOrders(): ResponseEntity<List<OrderEntity>> {
        val orders = orderService.getAllOrders()
        return ResponseEntity.ok(orders)
    }

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

    @GetMapping("/lookup")
    fun lookupOrder(
        @RequestParam email: String,
        @RequestParam orderId: Long
    ): ResponseEntity<OrderEntity> {
        val order = orderService.lookupOrder(email, orderId)
        return ResponseEntity.ok(order)
    }

    @PostMapping("/{id}/pay")
    fun payForOrder(@PathVariable id: Long): ResponseEntity<OrderEntity> {
        val updatedOrder = orderService.markAsPaid(id)
        return ResponseEntity.ok(updatedOrder)
    }
}
