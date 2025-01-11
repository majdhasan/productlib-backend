package com.meshhdawi.productlib.products

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.multipart.MultipartFile

data class ProductCreateRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val image: MultipartFile,
    val unit: ProductUnit,
    val translations: List<ProductTranslationRequest> = emptyList()
)

data class ProductTranslationRequest @JsonCreator constructor(
    @JsonProperty("language") val language: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String?
)