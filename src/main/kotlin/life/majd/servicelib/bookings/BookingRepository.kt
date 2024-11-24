package life.majd.servicelib.bookings

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

    @Query("SELECT b FROM BookingEntity b WHERE b.serviceEntity.id = :serviceId AND b.startTime < :endTime AND b.endTime > :startTime")
    fun findByServiceEntityAndTimeRange(
        @Param("serviceId") serviceId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<BookingEntity>
}
