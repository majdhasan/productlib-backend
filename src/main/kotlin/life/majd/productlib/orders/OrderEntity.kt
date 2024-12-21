package life.majd.productlib.orders

import jakarta.persistence.*
import life.majd.productlib.users.UserEntity
import life.majd.productlib.cart.CartEntity
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val notes: String? = null,

    @Column(name = "is_paid")
    val isPaid: Boolean = false,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    val customer: UserEntity,

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    val cart: CartEntity
)
