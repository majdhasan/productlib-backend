package com.meshhdawi.productlib.utils

object PhoneUtils {
    private val allowedRegex = Regex("^[+]?[0-9]*$")

    /**
     * Remove spaces and punctuation from the given phone number and make sure
     * it contains only digits with an optional leading plus sign. Throws
     * [IllegalArgumentException] if the resulting phone number is longer than
     * 15 characters as per E.164 standard.
     */
    fun sanitize(phone: String): String {
        val cleaned = phone.trim()
            .replace("(\\s|-|\u00A0|\\(|\))".toRegex(), "")
        if (!allowedRegex.matches(cleaned)) {
            throw IllegalArgumentException("Invalid phone number")
        }
        if (cleaned.length > 15) {
            throw IllegalArgumentException("Phone number too long")
        }
        return cleaned
    }
}
