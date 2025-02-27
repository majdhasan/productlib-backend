package com.meshhdawi.productlib.customexceptions

class TokenNotFoundException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : RuntimeException(message)

class TokenExpiredException(message: String) : RuntimeException(message)
