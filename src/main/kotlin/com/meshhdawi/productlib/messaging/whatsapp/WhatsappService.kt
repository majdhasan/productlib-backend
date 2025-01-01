package com.meshhdawi.productlib.messaging.whatsapp

import com.meshhdawi.productlib.AppProperties
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import org.springframework.stereotype.Service

@Service
class WhatsappService(
    appProperties: AppProperties
) {
    private val accountSid: String = appProperties.twilioConfig.accountSid
    private val authToken: String = appProperties.twilioConfig.authToken
    private val fromNumber: String = appProperties.twilioConfig.fromNumber

    init {
        Twilio.init(accountSid, authToken)
    }

    fun sendMessage(to: String, message: String): Message {
        return Message.creator(
            PhoneNumber("whatsapp:$to"),
            PhoneNumber("whatsapp:$fromNumber"),
            message
        ).create()
    }
}