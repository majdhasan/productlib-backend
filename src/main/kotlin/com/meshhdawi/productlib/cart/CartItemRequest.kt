package com.meshhdawi.productlib.cart

data class CartItemRequest(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val notes: String? = null
)
