package com.meshhdawi.productlib.customexceptions

data class ErrorResponse(
    val message: String,
    val errorCode: String? = null
)