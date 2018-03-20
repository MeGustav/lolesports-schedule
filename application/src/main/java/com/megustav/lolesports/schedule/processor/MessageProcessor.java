package com.megustav.lolesports.schedule.processor;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Interface defining message processing contract
 *
 * @author MeGustav
 *         15/02/2018 22:26
 */
public interface MessageProcessor {

    /**
     * Process incoming message and form response
     *
     * @param processingInfo enough data to process a message
     * @return formed response
     */
    BotApiMethod<Message> processIncomingMessage(ProcessingInfo processingInfo);

    /**
     * @return processor type
     */
    ProcessorType getType();

}
