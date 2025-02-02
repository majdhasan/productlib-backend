package com.meshhdawi.productlib.messaging.telegram

import com.fasterxml.jackson.annotation.JsonIgnore
import com.meshhdawi.productlib.users.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "telegram_admins")
data class TelegramAdminEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(name = "chat_id", nullable = false)
    val chatId: Long,

    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @JsonIgnore
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
