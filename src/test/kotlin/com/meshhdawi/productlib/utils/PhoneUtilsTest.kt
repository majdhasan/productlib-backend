package com.meshhdawi.productlib.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PhoneUtilsTest {
    @Test
    fun `sanitize converts arabic digits to english`() {
        val sanitized = PhoneUtils.sanitize("٠١٢٣٤٥٦٧٨٩")
        assertEquals("0123456789", sanitized)
    }
}

