package com.meshhdawi.productlib.orders

import java.time.LocalDateTime

data class OrderRequest(
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val userEmail: String,
)