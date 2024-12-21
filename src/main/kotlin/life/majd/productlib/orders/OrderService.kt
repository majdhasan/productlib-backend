package life.majd.productlib.orders

import life.majd.productlib.users.UserEntity
import life.majd.productlib.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class OrderService(
    private val repository: OrderRepository,
    private val userRepository: UserRepository
) {

    private fun createOrder(orderEntity: OrderEntity): OrderEntity {
        return repository.save(orderEntity)
    }

    private fun validateAndCreateGuestUser(request: OrderRequest): UserEntity {
        if (request.firstName.isNullOrBlank()) {
            throw IllegalArgumentException("Customer first name is missing")
        }
        if (request.lastName.isNullOrBlank()) {
            throw IllegalArgumentException("Customer last name is missing")
        }
        if (request.phoneNumber.isNullOrBlank()) {
            throw IllegalArgumentException("Customer phone number is missing")
        }

        return userRepository.save(
            UserEntity(
                firstName = request.firstName,
                lastName = request.lastName,
                email = request.userEmail,
                phoneNumber = request.phoneNumber,
                password = null,
                isRegistered = false,
                isVerified = false
            )
        )
    }

    fun getOrderById(id: Long): OrderEntity = repository.findById(id).orElseThrow {
        IllegalArgumentException("Order with ID $id not found.")
    }

    fun lookupOrder(email: String, orderId: Long): OrderEntity {
        val order = repository.findById(orderId).orElseThrow {
            IllegalArgumentException("Order with ID $orderId not found.")
        }

        if (order.customer.email != email) {
            throw IllegalArgumentException("Email does not match the order.")
        }

        return order
    }

    fun markAsPaid(id: Long): OrderEntity {
        val order = repository.findById(id).orElseThrow { IllegalArgumentException("Order with ID $id not found") }
        if (order.isPaid) {
            throw IllegalStateException("Order with ID $id is already paid.")
        }
        val updatedOrder = order.copy(isPaid = true, updatedAt = LocalDateTime.now())
        return repository.save(updatedOrder)
    }

    fun getAllOrders(): List<OrderEntity> = repository.findAll()

    fun getOrdersByCustomer(customerId: Long): List<OrderEntity> = repository.findAllByCustomerId(customerId)

    fun updateOrder(id: Long, updatedOrderEntity: OrderEntity): OrderEntity {
        val existingOrder = repository.findById(id).orElseThrow {
            IllegalArgumentException("Order with ID $id not found")
        }
        val orderToSave = existingOrder.copy(
            notes = updatedOrderEntity.notes,
            updatedAt = LocalDateTime.now(),
            customer = updatedOrderEntity.customer
        )
        return repository.save(orderToSave)
    }

}
