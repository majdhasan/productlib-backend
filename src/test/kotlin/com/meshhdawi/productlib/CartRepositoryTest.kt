package com.meshhdawi.productlib

import com.meshhdawi.productlib.cart.CartEntity
import com.meshhdawi.productlib.cart.CartRepository
import com.meshhdawi.productlib.users.UserEntity
import com.meshhdawi.productlib.users.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull

@SpringBootTest
class CartRepositoryTest(
    @Autowired private val cartRepository: CartRepository,
    @Autowired private val userRepository: UserRepository
) {

    @Test
    fun `test CartEntity persistence`() {
        // Create and save a UserEntity
        val user = userRepository.save(
            UserEntity(
                firstName = "John",
                lastName = "Doe",
                phoneNumber = "1234567890",
                email = "john.doe@example.com",
                isRegistered = true,
                isVerified = false
            )
        )

        // Create and save a CartEntity linked to the user
        val cart = cartRepository.save(CartEntity(user = user))

        // Assert that the cart was saved and has a generated ID
        assertNotNull(cart.id, "Cart ID should not be null after persisting.")
    }
}
