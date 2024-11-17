package life.majd.servicelib.calendar

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface CalendarEventRepository : JpaRepository<CalendarEvent, Long> {
    fun findAllByStartTimeBetween(start: LocalDateTime, end: LocalDateTime): List<CalendarEvent>
    fun findAllByServiceId(serviceId: Long): List<CalendarEvent>
    fun findAllByCustomerId(customerId: Long): List<CalendarEvent>
}
