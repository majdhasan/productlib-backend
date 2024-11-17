package life.majd.servicelib.services

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/services")
class ServiceController(private val service: ServiceService) {

    @PostMapping
    fun createService(@RequestBody service: Service): ResponseEntity<Service> {
        val createdService = this.service.createService(service)
        return ResponseEntity.ok(createdService)
    }

    @GetMapping
    fun getAllServices(): ResponseEntity<List<Service>> {
        val services = service.getAllServices()
        return ResponseEntity.ok(services)
    }

    @GetMapping("/user/{userId}")
    fun getServicesByUser(@PathVariable userId: Long): ResponseEntity<List<Service>> {
        val services = service.getServicesByUser(userId)
        return ResponseEntity.ok(services)
    }

    @PutMapping("/{id}")
    fun updateService(@PathVariable id: Long, @RequestBody updatedService: Service): ResponseEntity<Service> {
        val updated = service.updateService(id, updatedService)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteService(@PathVariable id: Long): ResponseEntity<Void> {
        service.deleteService(id)
        return ResponseEntity.noContent().build()
    }
}
