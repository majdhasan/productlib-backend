package com.meshhdawi.productlib.cart.cartitems

import com.fasterxml.jackson.annotation.JsonIgnore
import com.meshhdawi.productlib.cart.CartEntity
import jakarta.persistence.*
import com.meshhdawi.productlib.products.ProductEntity
import java.time.LocalDateTime

@Entity
@Table(name = "cart_items")
data class CartItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    val cart: CartEntity,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity,

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column
    var notes: String?,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
