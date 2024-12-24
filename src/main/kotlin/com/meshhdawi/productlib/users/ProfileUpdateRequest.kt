package com.meshhdawi.productlib.users

import java.time.LocalDate

data class ProfileUpdateRequest(
    val firstName: String?,
    val lastName: String?,
    val dateOfBirth: LocalDate?,
    val preferredLanguage: String?
)