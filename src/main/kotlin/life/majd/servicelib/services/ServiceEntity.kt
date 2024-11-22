package life.majd.servicelib.services

import jakarta.persistence.*
import life.majd.servicelib.users.UserEntity
import java.time.LocalDateTime

@Entity
@Table(name = "services")
data class ServiceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column
    val description: String? = null,

    @Column
    val location: String? = null,

    @Column(nullable = false)
    val cost: Double,

    @Column(nullable = false)
    val duration: Int,

    @Column
    val address: String? = null,

    @Column(nullable = true)
    val latitude: Double? = null,

    @Column(nullable = true)
    val longitude: Double? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val category: ServiceCategory,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val userEntity: UserEntity
)
