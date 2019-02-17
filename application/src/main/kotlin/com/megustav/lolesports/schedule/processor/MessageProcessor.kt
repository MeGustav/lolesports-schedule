package com.megustav.lolesports.schedule.processor

import freemarker.template.TemplateException
import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.objects.Message
import java.io.IOException

/**
 * Interface defining message processing contract
 */
interface MessageProcessor {

    val type: ProcessorType

    /**
     * Process incoming message and form response
     *
     * @param processingInfo enough data to process a message
     * @return formed response
     */
    @Throws(IOException::class, TemplateException::class)
    fun processIncomingMessage(processingInfo: ProcessingInfo): BotApiMethod<Message>

}