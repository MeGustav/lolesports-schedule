package com.megustav.lolesports.schedule.processor.impl

import com.megustav.lolesports.schedule.processor.MessageProcessor
import com.megustav.lolesports.schedule.processor.ProcessingInfo
import com.megustav.lolesports.schedule.processor.ProcessorRepository
import com.megustav.lolesports.schedule.processor.ProcessorType
import com.megustav.lolesports.schedule.riot.League
import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton

/**
 * Class for a start request processing
 */
class StartProcessor(
        /** Processor repository  */
        private val repository: ProcessorRepository
) : MessageProcessor {

    /**
     * Initialization
     */
    fun init() {
        repository.register(this)
    }

    override fun processIncomingMessage(processingInfo: ProcessingInfo): BotApiMethod<Message> {
        val buttons = League.values().map {
            InlineKeyboardButton(
                    it.officialName.toUpperCase()
            ).setCallbackData(
                     "${ProcessorType.UPCOMING.path} ${it.officialName}"
            )
        }
        val markup = InlineKeyboardMarkup().setKeyboard(listOf(buttons))
        return SendMessage()
                .setChatId(processingInfo.chatId)
                .setText("Upcoming matches for:")
                .setReplyMarkup(markup)
    }

    override val type: ProcessorType
        get() = ProcessorType.START
}
