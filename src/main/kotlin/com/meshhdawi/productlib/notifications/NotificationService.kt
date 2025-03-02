package com.meshhdawi.productlib.notifications

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class NotificationService(private val notificationRepository: NotificationRepository) {

    fun createNotification(title: String, message: String, userId: Long, orderId: Long? = null) {
        // Create a new notification
        notificationRepository.save(
            NotificationEntity(
                title = title,
                message = message,
                userId = userId,
                orderId = orderId
            )
        )
    }

    fun getNotifications(userId: Long): List<NotificationEntity> {
        return notificationRepository.findByUserId(userId)
    }

    @Transactional
    fun markNotificationsAsRead(userId: Long): List<NotificationEntity> {
        val notifications = notificationRepository.findByUserId(userId)
        val updatedNotifications = notifications.map { it.copy(isRead = true) }
        notificationRepository.saveAll(updatedNotifications)
        return updatedNotifications
    }

    // TODO expose in the controller
    fun deleteNotification(notificationId: Long) {
        notificationRepository.deleteById(notificationId)
    }
}