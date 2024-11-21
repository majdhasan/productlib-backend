package life.majd.servicelib.bookings

import life.majd.servicelib.services.ServiceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
@Transactional
class BookingService(private val repository: BookingRepository, private val serviceRepository: ServiceRepository) {

    fun createBooking(booking: Booking): Booking {
        validateBooking(booking)
        return repository.save(booking)
    }

    fun getBookingById(id: Long): Booking {
        return repository.findById(id).orElseThrow {
            IllegalArgumentException("Booking with ID $id not found.")
        }
    }

    fun lookupBooking(email: String, bookingId: Long): Booking {
        val booking = repository.findById(bookingId).orElseThrow {
            IllegalArgumentException("Booking with ID $bookingId not found.")
        }

        if (booking.customer.email != email) {
            throw IllegalArgumentException("Email does not match the booking.")
        }

        return booking
    }

    fun markAsPaid(id: Long): Booking {
        val booking = repository.findById(id).orElseThrow { IllegalArgumentException("Booking with ID $id not found") }
        if (booking.isPaid) {
            throw IllegalStateException("Booking with ID $id is already paid.")
        }
        val updatedBooking = booking.copy(isPaid = true, updatedAt = LocalDateTime.now())
        return repository.save(updatedBooking)
    }

    fun getAllBookings(): List<Booking> = repository.findAll()

    fun getBookingsByCustomer(customerId: Long): List<Booking> = repository.findAllByCustomerId(customerId)

    fun getBookingsByService(serviceId: Long): List<Booking> = repository.findAllByServiceId(serviceId)

    fun getBookingsBetween(start: LocalDateTime, end: LocalDateTime): List<Booking> =
        repository.findAllByStartTimeBetween(start, end)

    fun updateBooking(id: Long, updatedBooking: Booking): Booking {
        val existingBooking = repository.findById(id).orElseThrow {
            IllegalArgumentException("Booking with ID $id not found")
        }
        val bookingToSave = existingBooking.copy(
            notes = updatedBooking.notes,
            startTime = updatedBooking.startTime,
            endTime = updatedBooking.endTime,
            location = updatedBooking.location,
            updatedAt = LocalDateTime.now(),
            service = updatedBooking.service,
            customer = updatedBooking.customer
        )
        return repository.save(bookingToSave)
    }

    private fun validateBooking(booking: Booking) {
        if (booking.startTime.isAfter(booking.endTime)) {
            throw IllegalArgumentException("Start time cannot be after end time.")
        }

        val overlappingBookings = repository.findAllByServiceIdAndStartTimeBetween(
            booking.service.id,
            booking.startTime.minusSeconds(1),
            booking.endTime.plusSeconds(1)
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

        val bookedSlots = repository.findAllByServiceIdAndStartTimeBetween(
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
