package com.meshhdawi.productlib

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("meshhdawi")
data class AppProperties(
    val emailConfig: EmailConfig,
//    val paypalConfig: PaypalConfig,
//    val twilioConfig: TwilioConfig,
    ) {

    data class EmailConfig(
        val email: String,
    )

    data class PaypalConfig(
        val clientId: String,
        val clientSecret: String,
        val mode: String,
    )

    data class TwilioConfig(
        val accountSid: String,
        val authToken: String,
        val fromNumber: String,
    )
}