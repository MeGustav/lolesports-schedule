package com.megustav.lolesports.schedule.bot

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.LongPollingBot

/**
 * @author MeGustav
 *         14/11/2017 22:15
 */
class BotRegistry {

    companion object {
        private val log = LoggerFactory.getLogger(BotRegistry::class.java)
    }

    private val api: TelegramBotsApi

    init {
        ApiContextInitializer.init()
        this.api = TelegramBotsApi()
    }

    /**
     * Register a bot
     */
    fun register(bot: LongPollingBot) {
        try {
            log.info("Registering bot...")
            api.registerBot(bot)
            log.info("Bot registered")
        } catch (ex: TelegramApiException) {
            log.error("Error registering bot", ex)
        }

    }



}
