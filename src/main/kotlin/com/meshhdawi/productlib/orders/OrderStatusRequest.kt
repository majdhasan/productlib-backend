package com.meshhdawi.productlib.orders

data class OrderStatusRequest(
    val status: OrderStatus,
    val orderId: Long
)