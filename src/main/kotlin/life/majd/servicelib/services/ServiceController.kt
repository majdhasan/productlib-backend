package life.majd.servicelib.services

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/services")
class ServiceController(private val service: ServiceService) {

    @PostMapping
    fun createService(@RequestBody serviceEntity: ServiceEntity): ResponseEntity<ServiceEntity> {
        val createdService = this.service.createService(serviceEntity)
        return ResponseEntity.ok(createdService)
    }

    @GetMapping
    fun getAllServices(): ResponseEntity<List<ServiceEntity>> {
        val services = service.getAllServices()
        return ResponseEntity.ok(services)
    }

    @GetMapping("/user/{userId}")
    fun getServicesByUser(@PathVariable userId: Long): ResponseEntity<List<ServiceEntity>> {
        val services = service.getServicesByUser(userId)
        return ResponseEntity.ok(services)
    }

    @GetMapping("/{id}")
    fun getServiceById(@PathVariable id: Long): ResponseEntity<ServiceEntity> = ResponseEntity.ok(service.getServicesById(id))

    @PutMapping("/{id}")
    fun updateService(@PathVariable id: Long, @RequestBody updatedServiceEntity: ServiceEntity): ResponseEntity<ServiceEntity> {
        val updated = service.updateService(id, updatedServiceEntity)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteService(@PathVariable id: Long): ResponseEntity<Void> {
        service.deleteService(id)
        return ResponseEntity.noContent().build()
    }
}
