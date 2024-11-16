package life.majd.servicelib.calendar

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CalendarEventService(private val repository: CalendarEventRepository) {

    fun createEvent(event: CalendarEvent): CalendarEvent {
        validateEvent(event)
        return repository.save(event)
    }

    fun getAllEvents(): List<CalendarEvent> = repository.findAll()

    fun getEventsByUser(userId: Long): List<CalendarEvent> = repository.findAllByUserId(userId)

    fun getEventsByService(serviceId: Long): List<CalendarEvent> = repository.findAllByServiceId(serviceId)

    fun getEventsBetween(start: LocalDateTime, end: LocalDateTime): List<CalendarEvent> =
        repository.findAllByStartTimeBetween(start, end)

    fun updateEvent(id: Long, updatedEvent: CalendarEvent): CalendarEvent {
        val existingEvent = repository.findById(id).orElseThrow {
            IllegalArgumentException("Event with ID $id not found")
        }
        val eventToSave = existingEvent.copy(
            title = updatedEvent.title,
            description = updatedEvent.description,
            startTime = updatedEvent.startTime,
            endTime = updatedEvent.endTime,
            location = updatedEvent.location,
            isAllDay = updatedEvent.isAllDay,
            updatedAt = LocalDateTime.now(),
            service = updatedEvent.service
        )
        return repository.save(eventToSave)
    }

    fun deleteEvent(id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw IllegalArgumentException("Event with ID $id not found")
        }
    }

    private fun validateEvent(event: CalendarEvent) {
        if (event.startTime.isAfter(event.endTime)) {
            throw IllegalArgumentException("Start time cannot be after end time.")
        }
    }
}
