package com.meshhdawi.productlib.products

import org.springframework.web.multipart.MultipartFile

data class ProductCreateRequest(
    val name: String,
    val description: String?,
    val cost: Double,
    val image: MultipartFile,
    val category: ProductCategory,
    val translations: List<ProductTranslationRequest> = emptyList()
)

data class ProductTranslationRequest(
    val language: String,
    val name: String,
    val description: String?
)