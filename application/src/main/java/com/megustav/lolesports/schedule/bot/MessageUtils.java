package com.megustav.lolesports.schedule.bot;

import java.text.MessageFormat;
import java.util.function.Consumer;

/**
 * Various message utility methods
 *
 * @author MeGustav
 *         08/03/2018 16:47
 */
public final class MessageUtils {

    /**
     * Consume a chat message with a given consumer
     *
     * @param consumer consumer
     * @param chatId chat id
     * @param message message to consume
     */
    public static void consumeMessage(Consumer<String> consumer, Long chatId, String message) {
        consumer.accept(formChatMessage(chatId, message));
    }

    /**
     * Forms a chat-specific log message
     *
     * @param chatId chat id
     * @param text text
     * @return a chat-specific log message
     */
    public static String formChatMessage(Long chatId, String text) {
        return chatId == null ?
                "[unknown] " + text :
                MessageFormat.format("[{0}] {1}", chatId.toString(), text);
    }

    /** Utility class - instantiation restricted */
    public MessageUtils() { }
}
