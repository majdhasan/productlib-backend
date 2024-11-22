package life.majd.servicelib

import life.majd.servicelib.bookings.BookingEntity
import life.majd.servicelib.bookings.BookingRepository
import life.majd.servicelib.services.ServiceEntity
import life.majd.servicelib.services.ServiceCategory
import life.majd.servicelib.services.ServiceRepository
import life.majd.servicelib.users.UserEntity
import life.majd.servicelib.users.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class TestDataLoader(
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository,
    private val bookingRepository: BookingRepository
) {

    @Bean
    fun initDatabase(): CommandLineRunner {
        return CommandLineRunner {
            // Create Users
            val user1 = userRepository.save(
                UserEntity(
                    password = "password123",
                    email = "john@example.com",
                    firstName = "John",
                    lastName = "Doe",
                    dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0).toLocalDate()
                )
            )
            val user2 = userRepository.save(
                UserEntity(
                    password = "secure456",
                    email = "jane@example.com",
                    firstName = "Jane",
                    lastName = "Smith",
                    dateOfBirth = LocalDateTime.of(1992, 5, 12, 0, 0).toLocalDate()
                )
            )

            val user3 = userRepository.save(
                UserEntity(
                    password = "guestuser",
                    email = "guest@example.com",
                    firstName = "Guest",
                    lastName = "User",
                    dateOfBirth = LocalDateTime.of(2000, 6, 15, 0, 0).toLocalDate()
                )
            )

            // Create Services
            val services = listOf(
                ServiceEntity(
                    name = "Personal Training",
                    description = "One-hour personal training session",
                    cost = 50.0,
                    duration = 60,
                    address = "123 Fitness St",
                    latitude = 40.7128,
                    longitude = -74.0060,
                    userEntity = user1,
                    category = ServiceCategory.HAIRSTYLIST
                ),
                ServiceEntity(
                    name = "Consultation",
                    description = "30-minute professional consultation",
                    cost = 100.0,
                    duration = 30,
                    address = "456 Consulting Ave",
                    latitude = 34.0522,
                    longitude = -118.2437,
                    userEntity = user2,
                    category = ServiceCategory.PERSONAL_TRAINING
                ),
                ServiceEntity(
                    name = "Yoga Session",
                    description = "Relaxing one-hour yoga session",
                    cost = 40.0,
                    duration = 60,
                    address = "789 Zen Way",
                    latitude = 37.7749,
                    longitude = -122.4194,
                    userEntity = user1,
                    category = ServiceCategory.OTHER
                ),
                ServiceEntity(
                    name = "Haircut",
                    description = "Professional haircut and styling",
                    cost = 70.0,
                    duration = 45,
                    address = "321 Style Blvd",
                    latitude = 51.5074,
                    longitude = -0.1278,
                    userEntity = user2,
                    category = ServiceCategory.HAIRSTYLIST
                ),
                ServiceEntity(
                    name = "Massage Therapy",
                    description = "Therapeutic one-hour massage",
                    cost = 90.0,
                    duration = 60,
                    address = "654 Spa Road",
                    latitude = 48.8566,
                    longitude = 2.3522,
                    userEntity = user3,
                    category = ServiceCategory.OTHER
                )
            ).map { serviceRepository.save(it) }

            // Create Bookings
            val now = LocalDateTime.now()
            val bookings = listOf(
                BookingEntity(
                    notes = "Book a training session with John",
                    startTime = now.plus(1, ChronoUnit.DAYS).withHour(10).withMinute(0),
                    endTime = now.plus(1, ChronoUnit.DAYS).withHour(11).withMinute(0),
                    location = "John's Gym",
                    customer = user2,
                    serviceEntity = services[0]
                ),
                BookingEntity(
                    notes = "Book a consultation with Jane",
                    startTime = now.plus(3, ChronoUnit.DAYS).withHour(15).withMinute(0),
                    endTime = now.plus(3, ChronoUnit.DAYS).withHour(15).withMinute(30),
                    location = "Office",
                    customer = user1,
                    serviceEntity = services[1]
                ),
                BookingEntity(
                    notes = "Morning Yoga Session",
                    startTime = now.plus(2, ChronoUnit.DAYS).withHour(8).withMinute(0),
                    endTime = now.plus(2, ChronoUnit.DAYS).withHour(9).withMinute(0),
                    location = "Zen Studio",
                    customer = user3,
                    serviceEntity = services[2]
                ),
                BookingEntity(
                    notes = "Haircut appointment",
                    startTime = now.plus(5, ChronoUnit.DAYS).withHour(13).withMinute(0),
                    endTime = now.plus(5, ChronoUnit.DAYS).withHour(13).withMinute(45),
                    location = "Style Salon",
                    customer = user1,
                    serviceEntity = services[3]
                ),
                BookingEntity(
                    notes = "Massage Therapy",
                    startTime = now.minus(2, ChronoUnit.DAYS).withHour(14).withMinute(0),
                    endTime = now.minus(2, ChronoUnit.DAYS).withHour(15).withMinute(0),
                    location = "Spa Center",
                    customer = user2,
                    serviceEntity = services[4]
                )
            )
            bookingRepository.saveAll(bookings)
        }
    }
}
