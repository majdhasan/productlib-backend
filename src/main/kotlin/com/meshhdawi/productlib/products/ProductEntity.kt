package com.meshhdawi.productlib.products

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val translations: List<ProductTranslationEntity> = emptyList(),

    @Column
    val image: String,

    @Column
    val description: String? = null,

    @Column(nullable = false)
    val price: Double,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
