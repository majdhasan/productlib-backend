package com.meshhdawi.productlib.users

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(name = "phone_number", nullable = false, unique = true)
    var phoneNumber: String,

    @Column(name = "date_of_birth")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var dateOfBirth: LocalDate? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column
    @JsonIgnore
    var password: String? = null,

    @Column(name = "email_verified", nullable = false)
    @JsonIgnore
    var emailVerified: Boolean = false,

    @Column(name = "phone_verified", nullable = false)
    @JsonIgnore
    var phoneVerified: Boolean = false,

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.CUSTOMER,

    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @JsonIgnore
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
