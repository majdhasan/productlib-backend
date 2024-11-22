package life.majd.servicelib.bookings

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BookingRepository : JpaRepository<BookingEntity, Long> {
    fun findAllByServiceEntityId(serviceId: Long): List<BookingEntity>
    fun findAllByCustomerId(customerId: Long): List<BookingEntity>
    fun findAllByStartTimeBetween(start: LocalDateTime, end: LocalDateTime): List<BookingEntity>

    fun findAllByServiceEntityIdAndStartTimeBetween(
        serviceId: Long,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<BookingEntity>
}
