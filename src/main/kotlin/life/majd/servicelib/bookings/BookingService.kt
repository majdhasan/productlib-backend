package life.majd.servicelib.bookings

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class BookingService(private val repository: BookingRepository) {

    fun createBooking(booking: Booking): Booking {
        validateBooking(booking)
        return repository.save(booking)
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

    fun deleteBooking(id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw IllegalArgumentException("Booking with ID $id not found")
        }
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

}
