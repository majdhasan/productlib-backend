package com.meshhdawi.productlib.contact

data class ContactForm(
    val name: String?,
    val email: String?,
    val message: String
)
{
    fun toEntity():ContactFormEntity {
        return ContactFormEntity(
            name = name,
            email = email,
            message = message
        )
    }
}