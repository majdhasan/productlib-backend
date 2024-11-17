package life.majd.servicelib.calendar

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/calendar-events")
class CalendarEventController(private val service: CalendarEventService) {

    @PostMapping
    fun createEvent(@RequestBody event: CalendarEvent): ResponseEntity<CalendarEvent> {
        val createdEvent = service.createEvent(event)
        return ResponseEntity.ok(createdEvent)
    }

    @GetMapping
    fun getAllEvents(): ResponseEntity<List<CalendarEvent>> {
        val events = service.getAllEvents()
        return ResponseEntity.ok(events)
    }

    @GetMapping("/user/{customerId}")
    fun getEventsByCustomer(@PathVariable customerId: Long): ResponseEntity<List<CalendarEvent>> {
        val events = service.getEventsByCustomer(customerId)
        return ResponseEntity.ok(events)
    }

    @GetMapping("/service/{serviceId}")
    fun getEventsByService(@PathVariable serviceId: Long): ResponseEntity<List<CalendarEvent>> {
        val events = service.getEventsByService(serviceId)
        return ResponseEntity.ok(events)
    }

    @PutMapping("/{id}")
    fun updateEvent(@PathVariable id: Long, @RequestBody updatedEvent: CalendarEvent): ResponseEntity<CalendarEvent> {
        val updated = service.updateEvent(id, updatedEvent)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteEvent(@PathVariable id: Long): ResponseEntity<Void> {
        service.deleteEvent(id)
        return ResponseEntity.noContent().build()
    }
}
