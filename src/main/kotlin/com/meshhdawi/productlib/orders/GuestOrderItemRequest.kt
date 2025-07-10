package com.meshhdawi.productlib.orders

data class GuestOrderItemRequest(
    val productId: Long,
    val quantity: Int,
    val notes: String?
)
