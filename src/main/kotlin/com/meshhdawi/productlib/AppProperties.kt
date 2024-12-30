package com.meshhdawi.productlib

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("meshhdawi")
data class AppProperties(
    val emailConfig: EmailConfig,
    ) {
    data class EmailConfig(
        val email: String,
    )
}