package com.meshhdawi.productlib.products

import com.fasterxml.jackson.annotation.JsonIgnore
import com.meshhdawi.productlib.products.translations.ProductTranslationEntity
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

    @Column(nullable = false)
    val unit: ProductUnit = ProductUnit.PIECE,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val category: ProductCategory = ProductCategory.ROLL,

    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @JsonIgnore
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
