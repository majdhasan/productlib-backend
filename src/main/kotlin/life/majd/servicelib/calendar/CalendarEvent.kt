package life.majd.servicelib.calendar

import jakarta.persistence.*
import life.majd.servicelib.services.Service
import life.majd.servicelib.users.User
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_event")
data class CalendarEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,

    val description: String? = null,

    @Column(name = "start_time")
    val startTime: LocalDateTime,

    @Column(name = "end_time")
    val endTime: LocalDateTime,

    val location: String? = null,

    @Column(name = "is_all_day")
    val isAllDay: Boolean = false,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User, // The user who booked the service

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    val service: Service // The service being booked
)
