package com.meshhdawi.productlib.orders

import java.time.LocalDateTime

data class GuestOrderRequest(
    val orderType: OrderType,
    val address: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val orderNotes: String?,
    val wishedPickupTime: LocalDateTime?,
    val language: String?,
    val items: List<GuestOrderItemRequest>
)
