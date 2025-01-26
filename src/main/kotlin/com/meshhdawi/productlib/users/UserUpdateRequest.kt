package com.meshhdawi.productlib.users

data class UserUpdateRequest(
    val id: Long,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val agreeToReceiveMessages: Boolean
)