package life.majd.servicelib.bookings

import java.time.LocalDateTime

data class BookingRequest(
    val serviceId: Long,
    val userEmail: String,
    val startTime: LocalDateTime
)