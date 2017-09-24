package com.megustav.lolesports.schedule.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * A telegram bot providing information on the upcoming competitive League of Legends events
 *
 * @author MeGusav
 *         23.09.17 23:37
 */
public class LolEsportsScheduleBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);

    /** Bot Username */
    private static final String BOT_NAME = "LolEsportsScheduleBot";
    /**
     * Bot token. Obviously to be moved to config.
     * The value here is temporal for bot testing and is to be changed later.
     */
    private static final String BOT_TOKEN = "409557956:AAGeN4cSk9VBhtr3UpvJnCItKd1zLXb1eVk";

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (! update.hasMessage()) {
            log.debug("No message received");
            return;
        }

        Message received = update.getMessage();
        log.info("Received message: {}", received);
        try {

            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(received.getText().contains("Инн") ?
                            "Люблю тебя. Дай еще супа" :
                            "Not implemented yet");
            log.debug("Prepared response: {}", message);
            execute(message);
        } catch (Exception ex) {
            log.error("Error processing update", ex);
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
}
