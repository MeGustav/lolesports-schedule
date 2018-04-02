package com.megustav.lolesports.schedule.bot;

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
     * Forms chat id prefix
     *
     * @param chatId chat id
     * @return prefix with chat id
     */
    public static String formChatPrefix(Long chatId) {
        return "[" + (chatId == null ? "unknown" : chatId) + "]";
    }

    /**
     * Forms a chat-specific log message
     *
     * @param chatId chat id
     * @param text text
     * @return a chat-specific log message
     */
    private static String formChatMessage(Long chatId, String text) {
        return formChatPrefix(chatId) + " " + text;
    }

    /** Utility class - instantiation restricted */
    public MessageUtils() { }
}
