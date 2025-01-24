package com.meshhdawi.productlib.contact

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ContactFormRepository : JpaRepository<ContactFormEntity, Long>{

    fun getContactFormEntitiesByCreatedAtBetween(
        createdAtAfter: LocalDateTime,
        createdAtBefore: LocalDateTime
    ): MutableList<ContactFormEntity>
}