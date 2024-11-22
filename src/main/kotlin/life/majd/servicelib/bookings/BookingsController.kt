package life.majd.servicelib.bookings

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/bookings")
class BookingsController(
    private val bookingService: BookingService
) {

    @GetMapping
    fun getAllBookings(): ResponseEntity<List<BookingEntity>> {
        val bookings = bookingService.getAllBookings()
        return ResponseEntity.ok(bookings)
    }

    @GetMapping("/{id}")
    fun getBookingById(@PathVariable id: Long): ResponseEntity<BookingEntity> {
        val booking = bookingService.getBookingById(id)
        return ResponseEntity.ok(booking)
    }

    @GetMapping("/user/{customerId}")
    fun getBookingsByCustomer(@PathVariable customerId: Long): ResponseEntity<List<BookingEntity>> {
        val bookings = bookingService.getBookingsByCustomer(customerId)
        return ResponseEntity.ok(bookings)
    }

    @GetMapping("/lookup")
    fun lookupBooking(
        @RequestParam email: String,
        @RequestParam bookingId: Long
    ): ResponseEntity<BookingEntity> {
        val booking = bookingService.lookupBooking(email, bookingId)
        return ResponseEntity.ok(booking)
    }

    @GetMapping("/service/{serviceId}")
    fun getBookingsByService(@PathVariable serviceId: Long): ResponseEntity<List<BookingEntity>> {
        val bookings = bookingService.getBookingsByService(serviceId)
        return ResponseEntity.ok(bookings)
    }

    @PostMapping("/{id}/pay")
    fun payForBooking(@PathVariable id: Long): ResponseEntity<BookingEntity> {
        val updatedBooking = bookingService.markAsPaid(id)
        return ResponseEntity.ok(updatedBooking)
    }

    @PostMapping
    fun bookService(@RequestBody request: BookingRequest): ResponseEntity<BookingEntity> =
        ResponseEntity.ok(bookingService.bookService(request))


    @GetMapping("/service/{serviceId}/available-slots")
    fun getAvailableSlots(
        @PathVariable serviceId: Long,
        @RequestParam date: String
    ): ResponseEntity<List<String>> {
        val parsedDate = LocalDate.parse(date)
        val availableSlots = bookingService.getAvailableSlotsForService(serviceId, parsedDate)
        return ResponseEntity.ok(availableSlots)
    }
}
