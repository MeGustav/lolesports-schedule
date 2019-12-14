package com.megustav.lolesports.schedule.bot

import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author MeGustav
 *         08/03/2018 16:47
 */
class MessageUtils {

    companion object {

        @JvmStatic
        fun createMessage(): SendMessage = SendMessage().enableMarkdown(true)

        @JvmStatic
        fun createMessage(chatId: Long, message: String): SendMessage = SendMessage(chatId, message).enableMarkdown(true)

        @JvmStatic
        fun consumeMessage(consumer: (message: String) -> Unit, chatId: Long?, message: String) =
                consumer(formChatMessage(chatId, message))

        @JvmStatic
        fun formChatPrefix(chatId: Long?): String = "[$chatId]"

        private fun formChatMessage(chatId: Long?, text: String): String = "${formChatPrefix(chatId)} $text"
    }

}
