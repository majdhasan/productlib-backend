package com.meshhdawi.productlib.orders

import com.meshhdawi.productlib.context.getUserId
import com.meshhdawi.productlib.context.getUserRole
import com.meshhdawi.productlib.users.UserRole
import com.meshhdawi.productlib.web.security.AuthService
import com.meshhdawi.productlib.orders.GuestOrderRequest
import com.meshhdawi.productlib.orders.OrderEntity
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrdersController(
    private val orderService: OrderService,
    private val authService: AuthService
) {
    @GetMapping
    fun getAllOrders(request: HttpServletRequest): ResponseEntity<List<OrderEntity>> =
        authService.validateJWTAuth(request) {
            if (getUserRole() != UserRole.ADMIN) throw IllegalArgumentException("You are not authorized to do this operation.")
            ResponseEntity.ok(orderService.getAllOrders())
        }


    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<OrderEntity> =
        authService.validateJWTAuth(request) {
            val order = orderService.getOrderById(id)
            if (getUserRole() != UserRole.ADMIN && getUserId() != order.customerId?.id) {
                throw IllegalArgumentException("You are not authorized to view orders for this user.")
            }
            ResponseEntity.ok(order)
        }

    @GetMapping("/user/{customerId}")
    fun getOrdersByCustomer(
        @PathVariable customerId: Long,
        request: HttpServletRequest
    ): ResponseEntity<List<OrderEntity>> = authService.validateJWTAuth(request) {
        if (getUserId() != customerId) throw IllegalArgumentException("You are not authorized to view orders for this user.")
        val orders = orderService.getOrdersByCustomer(customerId)
        ResponseEntity.ok(orders)
    }

    @PostMapping
    fun createOrder(@RequestBody orderRequest: OrderRequest, request: HttpServletRequest): ResponseEntity<OrderEntity> =
        authService.validateJWTAuth(request) {
            if (getUserId() != orderRequest.customerId) throw IllegalArgumentException("You are not authorized to create orders for this user.")
            val createdOrder = orderService.createOrder(orderRequest)
            ResponseEntity.ok(createdOrder)
        }

    @PostMapping("/guest")
    fun createGuestOrder(@RequestBody orderRequest: GuestOrderRequest): ResponseEntity<OrderEntity> {
        val createdOrder = orderService.createGuestOrder(orderRequest)
        return ResponseEntity.ok(createdOrder)
    }

    @GetMapping("/guest/{id}")
    fun getGuestOrderById(
        @PathVariable id: Long,
        @RequestParam("lastName") lastName: String
    ): ResponseEntity<OrderEntity> {
        val order = orderService.getGuestOrderById(id, lastName)
        return ResponseEntity.ok(order)
    }

    @PutMapping("/guest/cancel/{id}")
    fun cancelGuestOrder(
        @PathVariable id: Long,
        @RequestParam("lastName") lastName: String
    ): ResponseEntity<OrderEntity> {
        val cancelledOrder = orderService.cancelGuestOrder(id, lastName)
        return ResponseEntity.ok(cancelledOrder)
    }

    @PutMapping("/status")
    fun updateOrderStatus(
        @RequestBody orderStatusRequest: OrderStatusRequest,
        request: HttpServletRequest
    ): ResponseEntity<OrderEntity> =
        authService.validateJWTAuth(request) {
            if (getUserRole() != UserRole.ADMIN) throw IllegalArgumentException("You are not authorized to do this operation.")
            val updatedOrder = orderService.updateOrderStatus(orderStatusRequest)
            ResponseEntity.ok(updatedOrder)
        }

    @PutMapping("/cancel/{id}")
    fun cancelOrder(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<OrderEntity> =
        authService.validateJWTAuth(request) {
            val order = orderService.getOrderById(id)
            if (getUserRole() != UserRole.ADMIN && getUserId() != order.customerId?.id) {
                throw IllegalArgumentException("You are not authorized to cancel orders for this user.")
            }
            val cancelledOrder = orderService.cancelOrder(id)
            ResponseEntity.ok(cancelledOrder)
        }
}
