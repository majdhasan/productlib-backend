package life.majd.servicelib.services

import org.springframework.data.jpa.repository.JpaRepository

interface ServiceRepository : JpaRepository<ServiceEntity, Long> {
    fun findByUserEntityId(userId: Long): List<ServiceEntity>
}
