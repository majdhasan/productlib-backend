package life.majd.servicelib

import life.majd.servicelib.calendar.CalendarEvent
import life.majd.servicelib.calendar.CalendarEventRepository
import life.majd.servicelib.services.Service
import life.majd.servicelib.services.ServiceRepository
import life.majd.servicelib.users.User
import life.majd.servicelib.users.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TestDataLoader(
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository,
    private val calendarEventRepository: CalendarEventRepository
) {

    @Bean
    fun initDatabase(): CommandLineRunner {
        return CommandLineRunner {
            // Create Users
            val user1 = userRepository.save(User(username = "john_doe", password = "password123", email = "john@example.com"))
            val user2 = userRepository.save(User(username = "jane_doe", password = "secure456", email = "jane@example.com"))

            // Create Services
            val service1 = serviceRepository.save(
                Service(
                    name = "Personal Training",
                    description = "One-hour personal training session",
                    cost = 50.0,
                    duration = 60,
                    user = user1
                )
            )
            val service2 = serviceRepository.save(
                Service(
                    name = "Consultation",
                    description = "30-minute professional consultation",
                    cost = 100.0,
                    duration = 30,
                    user = user2
                )
            )

            // Create Calendar Events (Bookings)
            calendarEventRepository.save(
                CalendarEvent(
                    title = "Training Session",
                    description = "Book a training session with John",
                    startTime = LocalDateTime.of(2024, 11, 20, 10, 0),
                    endTime = LocalDateTime.of(2024, 11, 20, 11, 0),
                    location = "John's Gym",
                    isAllDay = false,
                    user = user2, // Jane is booking the session
                    service = service1 // Booking John's service
                )
            )

            calendarEventRepository.save(
                CalendarEvent(
                    title = "Consultation Meeting",
                    description = "Book a consultation with Jane",
                    startTime = LocalDateTime.of(2024, 11, 22, 15, 0),
                    endTime = LocalDateTime.of(2024, 11, 22, 15, 30),
                    location = "Office",
                    isAllDay = false,
                    user = user1, // John is booking the session
                    service = service2 // Booking Jane's service
                )
            )
        }
    }
}
