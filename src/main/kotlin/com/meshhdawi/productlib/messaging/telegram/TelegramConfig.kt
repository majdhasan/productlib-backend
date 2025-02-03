package com.meshhdawi.productlib.messaging.telegram

import com.meshhdawi.productlib.AppProperties
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

@Configuration
class TelegramConfig(
    private val appProperties: AppProperties,
    private val telegramBot: TelegramBot,
) {
    @PostConstruct
    fun startTelegramBot() {
        if (appProperties.telegramToken.isBlank() || appProperties.telegramToken.contains("TELEGRAM_TOKEN")) {
            return
        }

        TelegramBotsLongPollingApplication().registerBot(appProperties.telegramToken, telegramBot)
    }
}
