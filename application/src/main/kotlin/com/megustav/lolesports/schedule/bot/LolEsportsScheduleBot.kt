package com.megustav.lolesports.schedule.bot

import com.megustav.lolesports.schedule.processor.ProcessingInfo
import com.megustav.lolesports.schedule.processor.ProcessorRepository
import com.megustav.lolesports.schedule.processor.ProcessorType
import org.slf4j.LoggerFactory
import org.springframework.core.task.TaskExecutor
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

/**
 * @author MeGustav
 *         23.09.17 23:37
 */
class LolEsportsScheduleBot(
        private val botName: String,
        private val botToken: String,
        private val botRegistry: BotRegistry,
        private val taskExecutor: TaskExecutor,
        private val repository: ProcessorRepository
) : TelegramLongPollingBot() {

    companion object {
        private val log = LoggerFactory.getLogger(LolEsportsScheduleBot::class.java)
    }

    fun init() {
        botRegistry.register(this)
    }

    override fun getBotUsername(): String = botName
    override fun getBotToken(): String = botToken
    override fun onUpdateReceived(update: Update) = taskExecutor.execute {
        try {
            processUpdate(update)
        } catch (ex: TelegramApiException) {
            log.error("Error processing an update: $update", ex)
        }
    }

    private fun processUpdate(update: Update) {
        val info = retrieveProcessingInfo(update) ?: run {
            log.error("Couldn't retrieve any processing information")
            return
        }

        val chatId = info.chatId

        val payload = info.payload ?: run {
            processBusinessError(chatId, "No payload received")
            return
        }

        val type = ProcessorType.fromRequest(payload) ?: run {
            processBusinessError(chatId, "Unsupported processor type: $payload")
            return
        }

        val processor = repository.getProcessor(type) ?: run {
            processBusinessError(chatId, "No processor registered for type: $type")
            return
        }

        // Sending response message
        try {
            execute(processor.processIncomingMessage(info))
        } catch (ex: Exception) {
            log.error("${MessageUtils.formChatPrefix(chatId)} Error processing message", ex)
            execute(SendMessage(chatId, "Internal server error"))
        }

    }

    private fun retrieveProcessingInfo(update: Update): ProcessingInfo? = when {
        update.hasMessage() -> update.message.let { ProcessingInfo(it.chatId, it.text) }
        update.hasCallbackQuery() -> update.callbackQuery.let { ProcessingInfo(it.message.chatId, it.data) }
        else -> null
    }

    private fun processBusinessError(chatId: Long, errorMessage: String) {
        MessageUtils.consumeMessage(log::error, chatId, errorMessage)
        execute(SendMessage(chatId, errorMessage))
    }

}
