package com.meshhdawi.productlib.products

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "product_translations")
data class ProductTranslationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    val product: ProductEntity,

    @Column(nullable = false)
    val language: String,

    @Column(nullable = false)
    val name: String,

    @Column
    val description: String? = null
)