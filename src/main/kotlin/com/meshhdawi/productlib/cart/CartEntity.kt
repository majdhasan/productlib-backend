package com.meshhdawi.productlib.cart

import com.fasterxml.jackson.annotation.JsonIgnore
import com.meshhdawi.productlib.cart.cartitems.CartItemEntity
import com.meshhdawi.productlib.users.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime
@Entity
@Table(name = "cart")
data class CartEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    val user: UserEntity,

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<CartItemEntity> = mutableListOf(),

    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @JsonIgnore
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
