package com.meshhdawi.productlib.orders

import java.time.LocalDateTime

data class OrderRequest(
    val cartId: Long,
    val customerId: Long,
    val orderType: OrderType,
    val address: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val orderNotes: String?,
    val wishedPickupTime: LocalDateTime?,
    val language: String?
)