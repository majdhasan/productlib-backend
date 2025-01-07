package com.meshhdawi.productlib.orders

import com.meshhdawi.productlib.context.getUserId
import com.meshhdawi.productlib.context.getUserRole
import com.meshhdawi.productlib.users.UserRole
import com.meshhdawi.productlib.web.security.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrdersController(
    private val orderService: OrderService,
    private val authService: AuthService
) {
    @GetMapping
    fun getAllOrders(request: HttpServletRequest): ResponseEntity<List<OrderEntity>> {
        return authService.validateJWTAuth(request) {
            if (getUserRole() != UserRole.ADMIN) throw IllegalArgumentException("You are not authorized to view all orders.")
            ResponseEntity.ok(orderService.getAllOrders())
        }
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<OrderEntity> {
        return authService.validateJWTAuth(request) {
            val order = orderService.getOrderById(id)
            if (getUserRole()!= UserRole.ADMIN && getUserId() != order.customerId.id) throw IllegalArgumentException("You are not authorized to view orders for this user.")
            ResponseEntity.ok(order)
        }
    }

    @GetMapping("/user/{customerId}")
    fun getOrdersByCustomer(
        @PathVariable customerId: Long,
        request: HttpServletRequest
    ): ResponseEntity<List<OrderEntity>> {
        return authService.validateJWTAuth(request) {
            if (getUserId() != customerId) throw IllegalArgumentException("You are not authorized to view orders for this user.")
            val orders = orderService.getOrdersByCustomer(customerId)
            ResponseEntity.ok(orders)
        }
    }

    @PostMapping
    fun createOrder(@RequestBody orderRequest: OrderRequest, request: HttpServletRequest): ResponseEntity<OrderEntity> =
        authService.validateJWTAuth(request) {
            if (getUserId() != orderRequest.customerId) throw IllegalArgumentException("You are not authorized to create orders for this user.")
            val createdOrder = orderService.createOrder(orderRequest)
            ResponseEntity.ok(createdOrder)
        }
}
