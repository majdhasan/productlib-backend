package com.meshhdawi.productlib.products

data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val duration: Int,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val userId: Long
)