package life.majd.servicelib.bookings

import life.majd.servicelib.customexceptions.SlotUnavailableException
import life.majd.servicelib.services.ServiceEntity
import life.majd.servicelib.services.ServiceRepository
import life.majd.servicelib.users.UserEntity
import life.majd.servicelib.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
@Transactional
class BookingService(
    private val repository: BookingRepository,
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository
) {

    private fun createBooking(bookingEntity: BookingEntity): BookingEntity {
        validateBooking(bookingEntity)
        return repository.save(bookingEntity)
    }

    fun bookService(request: BookingRequest): BookingEntity {
        val service = serviceRepository.findById(request.serviceId)
            .orElseThrow { IllegalArgumentException("Service with ID ${request.serviceId} not found") }

        val userEntity = userRepository.findByEmail(request.userEmail)
            ?: validateAndCreateGuestUser(request)

        validateSlotAvailability(request.startTime, service)

        val booking = BookingEntity(
            startTime = request.startTime,
            endTime = request.startTime.plusMinutes(service.duration.toLong()),
            location = service.location,
            serviceEntity = service,
            customer = userEntity
        )
        return createBooking(booking)
    }

    private fun validateSlotAvailability(startTime: LocalDateTime, service: ServiceEntity) {
        val overlappingBookings = repository.findByServiceEntityAndTimeRange(
            serviceId = service.id,
            startTime = startTime,
            endTime = startTime.plusMinutes(service.duration.toLong())
        )

        if (overlappingBookings.isNotEmpty()) {
            throw SlotUnavailableException("The selected slot is no longer available.")
        }
    }

    private fun validateAndCreateGuestUser(request: BookingRequest): UserEntity {
        if (request.firstName.isNullOrBlank()) {
            throw IllegalArgumentException("Customer first name is missing")
        }
        if (request.lastName.isNullOrBlank()) {
            throw IllegalArgumentException("Customer last name is missing")
        }

        return userRepository.save(
            UserEntity(
                firstName = request.firstName,
                lastName = request.lastName,
                email = request.userEmail,
                password = null, // Guests don't have passwords
                isRegistered = false,
                isVerified = false
            )
        )
    }


    fun getBookingById(id: Long): BookingEntity = repository.findById(id).orElseThrow {
        IllegalArgumentException("Booking with ID $id not found.")
    }

    fun lookupBooking(email: String, bookingId: Long): BookingEntity {
        val booking = repository.findById(bookingId).orElseThrow {
            IllegalArgumentException("Booking with ID $bookingId not found.")
        }

        if (booking.customer.email != email) {
            throw IllegalArgumentException("Email does not match the booking.")
        }

        return booking
    }

    fun markAsPaid(id: Long): BookingEntity {
        val booking = repository.findById(id).orElseThrow { IllegalArgumentException("Booking with ID $id not found") }
        if (booking.isPaid) {
            throw IllegalStateException("Booking with ID $id is already paid.")
        }
        val updatedBooking = booking.copy(isPaid = true, updatedAt = LocalDateTime.now())
        return repository.save(updatedBooking)
    }

    fun getAllBookings(): List<BookingEntity> = repository.findAll()

    fun getBookingsByCustomer(customerId: Long): List<BookingEntity> = repository.findAllByCustomerId(customerId)

    fun getBookingsByService(serviceId: Long): List<BookingEntity> = repository.findAllByServiceEntityId(serviceId)

    fun updateBooking(id: Long, updatedBookingEntity: BookingEntity): BookingEntity {
        val existingBooking = repository.findById(id).orElseThrow {
            IllegalArgumentException("Booking with ID $id not found")
        }
        val bookingToSave = existingBooking.copy(
            notes = updatedBookingEntity.notes,
            startTime = updatedBookingEntity.startTime,
            endTime = updatedBookingEntity.endTime,
            location = updatedBookingEntity.location,
            updatedAt = LocalDateTime.now(),
            serviceEntity = updatedBookingEntity.serviceEntity,
            customer = updatedBookingEntity.customer
        )
        return repository.save(bookingToSave)
    }

    private fun validateBooking(bookingEntity: BookingEntity) {
        if (bookingEntity.startTime.isAfter(bookingEntity.endTime)) {
            throw IllegalArgumentException("Start time cannot be after end time.")
        }

        val overlappingBookings = repository.findAllByServiceEntityIdAndStartTimeBetween(
            bookingEntity.serviceEntity.id,
            bookingEntity.startTime.minusSeconds(1),
            bookingEntity.endTime.plusSeconds(1)
        )
        if (overlappingBookings.isNotEmpty()) {
            throw IllegalArgumentException("The service is already booked for this time slot.")
        }
    }

    fun getAvailableSlotsForService(serviceId: Long, date: LocalDate): List<String> {
        val service = serviceRepository.findById(serviceId)
            .orElseThrow { IllegalArgumentException("Service not found") }

        val duration = service.duration // Duration of the service in minutes
        val startOfDay = LocalTime.of(9, 0) // Operating hours start at 9:00 AM
        val endOfDay = LocalTime.of(17, 0) // Operating hours end at 5:00 PM

        val allSlots = generateTimeSlots(startOfDay, endOfDay, duration)

        val bookedSlots = repository.findAllByServiceEntityIdAndStartTimeBetween(
            serviceId,
            date.atStartOfDay(),
            date.atTime(23, 59, 59)
        ).map { it.startTime.toLocalTime() }

        return allSlots.filter { slot -> !bookedSlots.contains(slot) }
            .map { it.format(DateTimeFormatter.ofPattern("HH:mm")) }
    }

    private fun generateTimeSlots(
        start: LocalTime,
        end: LocalTime,
        duration: Int
    ): List<LocalTime> {
        val slots = mutableListOf<LocalTime>()
        var currentTime = start
        while (currentTime.plusMinutes(duration.toLong()) <= end) {
            slots.add(currentTime)
            currentTime = currentTime.plusMinutes(duration.toLong())
        }
        return slots
    }

}
