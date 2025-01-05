package com.meshhdawi.productlib.orders

import com.meshhdawi.productlib.context.getUserId
import com.meshhdawi.productlib.web.security.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrdersController(
    private val orderService: OrderService,
    private val authService: AuthService
) {

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<OrderEntity> {
        return authService.validateJWTAuth(request) {
            val order = orderService.getOrderById(id)
            if (getUserId() != order.customerId.id) throw IllegalArgumentException("You are not authorized to view orders for this user.")
            ResponseEntity.ok(order)
        }
    }

    @GetMapping("/user/{customerId}")
    fun getOrdersByCustomer(@PathVariable customerId: Long, request: HttpServletRequest): ResponseEntity<List<OrderEntity>> {
        return authService.validateJWTAuth(request) {
            if (getUserId() != customerId) throw IllegalArgumentException("You are not authorized to view orders for this user.")
            val orders = orderService.getOrdersByCustomer(customerId)
            ResponseEntity.ok(orders)
        }
    }
    @PostMapping
    fun createOrder(@RequestBody orderRequest: OrderRequest): ResponseEntity<OrderEntity> {
        if (getUserId() != orderRequest.customerId) throw IllegalArgumentException("You are not authorized to view orders for this user.")
        val createdOrder = orderService.createOrder(orderRequest)
        return ResponseEntity.ok(createdOrder)
    }
}
