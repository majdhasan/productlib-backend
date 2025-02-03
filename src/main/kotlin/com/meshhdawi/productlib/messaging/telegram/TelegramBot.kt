package com.meshhdawi.productlib.messaging.telegram

import com.meshhdawi.productlib.AppProperties
import org.springframework.stereotype.Service
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

@Service
class TelegramBot(appProperties: AppProperties) : LongPollingSingleThreadUpdateConsumer {

    private var telegramClient: TelegramClient = OkHttpTelegramClient(appProperties.telegramToken)

    fun sendMessage(chatId: Long, message: String) {
        val sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(message)
            .build()

        try {
            telegramClient.execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    override fun consume(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val messageText = update.message.text
            val chatId = update.message.chatId

            println("Received message: $messageText from chatId: $chatId")

            val answer: String = """
                اهلا وسهلا فيك من مخبز المشهداوي.
                رقم التشات الخاص بك هو: 
                $chatId
ابعت الرقم لاحد عمال مخبز المشهداوي مع ذكر ايميلك لاضافتك للخدمه.
                شكرا لاختياركم مخبز المشهداوي.
            """.trimIndent()
            val message = SendMessage.builder()
                .chatId(chatId)
                .text(answer)
                .build()
            try {
                telegramClient.execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }
}
