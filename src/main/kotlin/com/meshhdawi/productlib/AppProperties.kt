package com.meshhdawi.productlib

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("meshhdawi")
data class AppProperties(
    val emailConfig: EmailConfig,
    val fileStorage: FileStorage,
    val jwtSecret: String,
) {

    data class EmailConfig(
        val email: String,
    )

    data class FileStorage(
        val uploadDir: String,
    )

}