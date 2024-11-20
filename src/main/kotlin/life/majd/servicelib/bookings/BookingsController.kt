package life.majd.servicelib.bookings

import life.majd.servicelib.services.ServiceRepository
import life.majd.servicelib.users.User
import life.majd.servicelib.users.UserRepository
import org.springframework.http.CacheControl
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
    private val bookingService: BookingService,
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository
) {

    @GetMapping
    fun getAllBookings(): ResponseEntity<List<Booking>> {
        val bookings = bookingService.getAllBookings()
        return ResponseEntity.ok(bookings)
    }

    @GetMapping("/{id}")
    fun getBookingById(@PathVariable id: Long?): ResponseEntity<Booking> {
        val booking = bookingService.getBookingById(id!!)
        return ResponseEntity.ok()
            .body(booking)
    }

    @GetMapping("/user/{customerId}")
    fun getBookingsByCustomer(@PathVariable customerId: Long): ResponseEntity<List<Booking>> {
        val bookings = bookingService.getBookingsByCustomer(customerId)
        return ResponseEntity.ok(bookings)
    }

    @GetMapping("/service/{serviceId}")
    fun getBookingsByService(@PathVariable serviceId: Long): ResponseEntity<List<Booking>> {
        val bookings = bookingService.getBookingsByService(serviceId)
        return ResponseEntity.ok(bookings)
    }

    @PostMapping("/{id}/pay")
    fun payForBooking(@PathVariable id: Long): ResponseEntity<Booking> {
        val updatedBooking = bookingService.markAsPaid(id)
        return ResponseEntity.ok(updatedBooking)
    }

    @PostMapping
    fun bookService(@RequestBody request: BookingRequest): ResponseEntity<Booking> {
        val service = serviceRepository.findById(request.serviceId)
            .orElseThrow { IllegalArgumentException("Service not found") }

        val user =
            userRepository.findByEmail(request.userEmail) ?: userRepository.save(
                User(
                    email = request.userEmail,
                    password = null,
                    isRegistered = false,
                    isVerified = false
                )
            )

        val event = bookingService.createBooking(
            Booking(
                startTime = request.startTime,
                endTime = request.startTime.plusMinutes(service.duration.toLong()),
                location = service.location,
                service = service,
                customer = user
            )
        )

        return ResponseEntity.ok(event)
    }

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
