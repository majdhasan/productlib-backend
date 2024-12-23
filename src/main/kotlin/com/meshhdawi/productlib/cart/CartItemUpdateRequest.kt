package com.meshhdawi.productlib.cart

data class CartItemUpdateRequest(
    val productId: Long,
    val quantity: Int,
    val notes: String? = null
)