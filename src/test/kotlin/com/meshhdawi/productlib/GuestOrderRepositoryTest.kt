import com.meshhdawi.productlib.orders.OrderEntity
import com.meshhdawi.productlib.orders.OrderRepository
import com.meshhdawi.productlib.orders.OrderType
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
class GuestOrderRepositoryTest(
    @Autowired private val orderRepository: OrderRepository
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
        orderRepository.deleteAll()
    }

    @Test
    fun `find guest order by id and last name`() {
        val order = orderRepository.save(
            OrderEntity(
                customerId = null,
                phone = "1234567890",
                firstName = "Guest",
                lastName = "Smith",
                address = "123 Street",
                wishedPickupTime = null,
                type = OrderType.PICKUP
            )
        )

        val fetched = orderRepository.findByIdAndLastNameAndCustomerIdIsNull(order.id, "Smith")

        assertNotNull(fetched)
    }
}
