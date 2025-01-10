package com.meshhdawi.productlib.orders

import com.meshhdawi.productlib.cart.CartRepository
import com.meshhdawi.productlib.cart.CartService
import com.meshhdawi.productlib.orders.orderitems.OrderItemEntity
import com.meshhdawi.productlib.orders.orderitems.OrderItemRepository
import com.meshhdawi.productlib.users.UserRepository
import com.meshhdawi.productlib.users.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val cartService: CartService,
    private val orderItemRepository: OrderItemRepository
) {

    fun getAllOrders(): List<OrderEntity> {
        return orderRepository.findAll()
    }

    fun getOrderById(id: Long): OrderEntity {
        return orderRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Order not found with ID: $id") }
    }

    fun getOrdersByCustomer(customerId: Long): List<OrderEntity> {
        val user = userService.getUserById(customerId)
        return orderRepository.findByCustomerId(user)
    }

    @Transactional
    fun createOrder(orderRequest: OrderRequest): OrderEntity {
        // Fetch and validate the cart
        val cart = cartRepository.findById(orderRequest.cartId)
            .orElseThrow { IllegalArgumentException("Cart not found with ID: ${orderRequest.cartId}") }

        // Fetch and validate the customer
        val customer = userRepository.findById(orderRequest.customerId)
            .orElseThrow { IllegalArgumentException("Customer not found with ID: ${orderRequest.customerId}") }

        if (cart.items.isEmpty()) {
            throw IllegalStateException("Cart cannot be empty.")
        }

        val savedOrder = orderRepository.save(
            OrderEntity(
                customerId = customer,
                type = orderRequest.orderType,
                address = orderRequest.address,
                phone = orderRequest.phone,
                firstName = orderRequest.firstName,
                lastName = orderRequest.lastName,
                notes = orderRequest.orderNotes,
                wishedPickupTime = orderRequest.wishedPickupTime,
            )
        )

        // Map cart items to order items and save them
        val orderItems = cart.items.map { cartItem ->
            orderItemRepository.save(
                OrderItemEntity(
                    order = savedOrder,
                    productId = cartItem.product.id,
                    productName = cartItem.product.name,
                    productDescription = cartItem.product.description,
                    productPrice = cartItem.product.price,
                    quantity = cartItem.quantity,
                    notes = cartItem.notes
                )
            )
        }


        savedOrder.items.addAll(orderItems)
        cartService.emptyCart(cart)

        return savedOrder
    }
}
