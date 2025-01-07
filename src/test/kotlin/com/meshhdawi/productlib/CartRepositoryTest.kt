package com.meshhdawi.productlib

import com.meshhdawi.productlib.cart.CartEntity
import com.meshhdawi.productlib.cart.CartRepository
import com.meshhdawi.productlib.users.UserEntity
import com.meshhdawi.productlib.users.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertNotNull

@Testcontainers
@SpringBootTest
class CartRepositoryTest(
    @Autowired private val cartRepository: CartRepository,
    @Autowired private val userRepository: UserRepository
) {

    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15.2").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerPgProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
        }
    }

    @BeforeEach
    fun setUp() {
        cartRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `test CartEntity persistence`() {
        val user = userRepository.save(
            UserEntity(
                firstName = "John",
                lastName = "Doe",
                phoneNumber = "1234567890",
                email = "john.doe@example.com",
            )
        )

        val cart = cartRepository.save(CartEntity(user = user))

        assertNotNull(cart.id, "Cart ID should not be null after persisting.")
    }
}