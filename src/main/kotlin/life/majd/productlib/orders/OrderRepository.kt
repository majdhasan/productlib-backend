package life.majd.productlib.orders

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface OrderRepository : JpaRepository<OrderEntity, Long> {

    fun findAllByCustomerId(customerId: Long): List<OrderEntity>
}
