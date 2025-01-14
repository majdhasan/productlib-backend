package com.meshhdawi.productlib.users.verification

import com.fasterxml.jackson.annotation.JsonIgnore
import com.meshhdawi.productlib.users.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "verification_tokens")
data class VerificationTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val token: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(name = "expires_at", nullable = false, updatable = false)
    val expiresAt: LocalDateTime = LocalDateTime.now().plusDays(1),

    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @JsonIgnore
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
