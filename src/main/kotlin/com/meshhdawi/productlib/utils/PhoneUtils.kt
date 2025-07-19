package com.meshhdawi.productlib.utils

object PhoneUtils {
    private val allowedRegex = Regex("^\\+?[0-9]{1,15}$")

    private val arabicToEnglishDigits = mapOf(
        '٠' to '0',
        '١' to '1',
        '٢' to '2',
        '٣' to '3',
        '٤' to '4',
        '٥' to '5',
        '٦' to '6',
        '٧' to '7',
        '٨' to '8',
        '٩' to '9'
    )

    /**
     * Remove spaces and punctuation from the given phone number and make sure
     * it contains only digits with an optional leading plus sign. Throws
     * [IllegalArgumentException] if the resulting phone number is longer than
     * 15 characters as per E.164 standard.
     */
    fun sanitize(phone: String): String {
        val normalized = phone.fold(StringBuilder()) { sb, char -> 
            sb.append(arabicToEnglishDigits[char] ?: char) 
        }.toString()
        val cleaned = normalized.trim().replace("[^+0-9]".toRegex(), "")
        if (!allowedRegex.matches(cleaned)) {
            throw IllegalArgumentException(
                "Invalid phone number: must contain only digits, may start with an optional '+', and be 1-15 characters long."
            )
        }
        return cleaned
    }
}
