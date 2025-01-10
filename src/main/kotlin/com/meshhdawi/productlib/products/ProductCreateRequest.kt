package com.meshhdawi.productlib.products

data class ProductCreateRequest(
    val name: String,
    val description: String?,
    val cost: Double,
    val image: String,
    val category: ProductCategory,
    val translations: List<ProductTranslationRequest> = emptyList()
)

data class ProductTranslationRequest(
    val language: String,
    val name: String,
    val description: String?
)