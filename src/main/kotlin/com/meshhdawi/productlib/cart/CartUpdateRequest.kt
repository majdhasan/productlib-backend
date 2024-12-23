package com.meshhdawi.productlib.cart

data class CartUpdateRequest(
    val items: List<CartItemUpdateRequest>,
    val status: String
)