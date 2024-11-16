package life.majd.servicelib.services

import org.springframework.data.jpa.repository.JpaRepository

interface ServiceRepository : JpaRepository<Service, Long> {
    fun findByUserId(userId: Long): List<Service>
}
