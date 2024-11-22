package life.majd.servicelib.services

import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@org.springframework.stereotype.Service
@Transactional
class ServiceService(private val repository: ServiceRepository) {

    fun createService(serviceEntity: ServiceEntity): ServiceEntity {
        validateService(serviceEntity)
        return repository.save(serviceEntity)
    }

    fun getAllServices(): List<ServiceEntity> = repository.findAll()

    fun getServicesByUser(userId: Long): List<ServiceEntity> = repository.findByUserEntityId(userId)

    fun getServicesById(serviceId: Long): ServiceEntity =
        repository.findById(serviceId).orElseThrow { IllegalArgumentException("Service with ID $serviceId not found") }

    fun updateService(id: Long, updatedServiceEntity: ServiceEntity): ServiceEntity {
        val existingService = repository.findById(id).orElseThrow {
            IllegalArgumentException("Service with ID $id not found")
        }
        val serviceToSave = existingService.copy(
            name = updatedServiceEntity.name,
            description = updatedServiceEntity.description,
            cost = updatedServiceEntity.cost,
            duration = updatedServiceEntity.duration,
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

    private fun validateService(serviceEntity: ServiceEntity) {
        if (serviceEntity.cost <= 0) {
            throw IllegalArgumentException("Cost must be greater than zero.")
        }
        if (serviceEntity.duration <= 0) {
            throw IllegalArgumentException("Duration must be greater than zero.")
        }
    }
}
