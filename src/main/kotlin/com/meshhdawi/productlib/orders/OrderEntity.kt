package com.meshhdawi.productlib.orders

import com.fasterxml.jackson.annotation.JsonIgnore
import com.meshhdawi.productlib.cart.CartEntity
import com.meshhdawi.productlib.orders.orderitems.OrderItemEntity
import com.meshhdawi.productlib.users.UserEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val notes: String? = null,

    @Column(name = "is_paid")
    val isPaid: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    val customerId: UserEntity,

    @Column(nullable = false)
    val phone: String,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false)
    val address: String,

    @Column
    val wishedPickupTime: LocalDateTime?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.SUBMITTED,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: OrderType = OrderType.PICKUP,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<OrderItemEntity> = mutableListOf(),

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @JsonIgnore
    val updatedAt: LocalDateTime = LocalDateTime.now()
)