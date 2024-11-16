package life.majd.servicelib.services

import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@org.springframework.stereotype.Service
@Transactional
class ServiceService(private val repository: ServiceRepository) {

    fun createService(service: Service): Service {
        validateService(service)
        return repository.save(service)
    }

    fun getAllServices(): List<Service> = repository.findAll()

    fun getServicesByUser(userId: Long): List<Service> = repository.findByUserId(userId)

    fun updateService(id: Long, updatedService: Service): Service {
        val existingService = repository.findById(id).orElseThrow {
            IllegalArgumentException("Service with ID $id not found")
        }
        val serviceToSave = existingService.copy(
            name = updatedService.name,
            description = updatedService.description,
            cost = updatedService.cost,
            duration = updatedService.duration,
            updatedAt = LocalDateTime.now()
        )
        return repository.save(serviceToSave)
    }

    fun deleteService(id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw IllegalArgumentException("Service with ID $id not found")
        }
    }

    private fun validateService(service: Service) {
        if (service.cost <= 0) {
            throw IllegalArgumentException("Cost must be greater than zero.")
        }
        if (service.duration <= 0) {
            throw IllegalArgumentException("Duration must be greater than zero.")
        }
    }
}
