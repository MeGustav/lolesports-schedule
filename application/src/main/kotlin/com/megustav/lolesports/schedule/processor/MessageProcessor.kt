package com.megustav.lolesports.schedule.processor

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

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
    fun processIncomingMessage(processingInfo: ProcessingInfo): BotApiMethod<Message>

}