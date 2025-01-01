package com.meshhdawi.productlib.users

data class CreateUserRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phoneNumber: String
)