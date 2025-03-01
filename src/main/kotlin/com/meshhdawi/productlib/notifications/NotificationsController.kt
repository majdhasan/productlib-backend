package com.meshhdawi.productlib.notifications

import com.meshhdawi.productlib.context.getUserId
import com.meshhdawi.productlib.web.security.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notifications")
class NotificationsController(
    private val notificationService: NotificationService,
    private val authService: AuthService
) {

    @GetMapping("/user")
    fun getNotificationsByUser(
        request: HttpServletRequest
    ): ResponseEntity<List<NotificationEntity>> =
        authService.validateJWTAuth(request) {
            ResponseEntity.ok(notificationService.getNotifications(getUserId()))
        }

    @PutMapping("/user/read")
    fun markNotificationsAsRead(request: HttpServletRequest): ResponseEntity<Any> =
        authService.validateJWTAuth(request) {
            notificationService.markNotificationsAsRead(getUserId())
            ResponseEntity.ok().build()
        }
}
