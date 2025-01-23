package com.meshhdawi.productlib.products

data class ProductUpdateRequest (
    val name: String,
    val description: String?,
    val price: Double,
    val unit: ProductUnit,
    val category: ProductCategory,
)