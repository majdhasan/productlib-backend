package life.majd.servicelib.bookings

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BookingRepository : JpaRepository<Booking, Long> {
    fun findAllByServiceId(serviceId: Long): List<Booking>
    fun findAllByCustomerId(customerId: Long): List<Booking>
    fun findAllByStartTimeBetween(start: LocalDateTime, end: LocalDateTime): List<Booking>

    fun findAllByServiceIdAndStartTimeBetween(
        serviceId: Long,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Booking>
}
