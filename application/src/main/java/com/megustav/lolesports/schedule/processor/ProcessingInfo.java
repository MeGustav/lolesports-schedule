package com.megustav.lolesports.schedule.processor;

/**
 * Basic info for message processing
 *
 * @author MeGustav
 *         19/03/2018 22:57
 */
public class ProcessingInfo {

    /** Chat id */
    private final long chatId;
    /** Message payload */
    private final String payload;

    public ProcessingInfo(Long chatId, String payload) {
        this.chatId = chatId;
        this.payload = payload;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "ProcessingInfo{" +
                "chatId=" + chatId +
                ", payload='" + payload + '\'' +
                '}';
    }
}
