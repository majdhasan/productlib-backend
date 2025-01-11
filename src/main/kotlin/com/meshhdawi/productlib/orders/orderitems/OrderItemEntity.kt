package com.meshhdawi.productlib.orders.orderitems

import com.fasterxml.jackson.annotation.JsonIgnore
import com.meshhdawi.productlib.orders.OrderEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "order_items")
data class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    val order: OrderEntity,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val productName: String,

    @Column(nullable = false)
    val productDescription: String?,

    @Column(nullable = false)
    val productPrice: Double,

    @Column(nullable = false)
    val productImage: String,

    @Column(nullable = false)
    val quantity: Int,

    @Column
    val notes: String?,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)