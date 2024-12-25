package com.meshhdawi.productlib.orders
import com.meshhdawi.productlib.cart.CartItemService
import com.meshhdawi.productlib.cart.CartRepository
import com.meshhdawi.productlib.cart.CartStatus
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
    private val cartItemService: CartItemService
) {

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

        // Fetch and validate the cart
        val customer = userRepository.findById(orderRequest.customerId)
            .orElseThrow { IllegalArgumentException("Customer not found with ID: ${orderRequest.customerId}") }

        if (cart.status != CartStatus.PENDING) {
            throw IllegalStateException("Cart is not in PENDING status.")
        }

        // Create the order
        val newOrder = OrderEntity(
            cart = cart,
            customerId = customer,
            type = orderRequest.orderType,
            address = orderRequest.address,
            phone = orderRequest.phone,
            firstName = orderRequest.firstName,
            lastName = orderRequest.lastName,
            notes = orderRequest.orderNotes,
            wishedPickupTime = orderRequest.wishedPickupTime,
        )

        // Save the order
        val savedOrder = orderRepository.save(newOrder)

        cartItemService.populateCartItemPrices(cart)

        // Update the cart status
        cart.status = CartStatus.ORDERED
        cartRepository.save(cart)

        return savedOrder
    }
}
