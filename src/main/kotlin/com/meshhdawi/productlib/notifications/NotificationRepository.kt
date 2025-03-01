package com.meshhdawi.productlib.notifications

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    fun findByUserId(userId: Long): List<NotificationEntity>
}
