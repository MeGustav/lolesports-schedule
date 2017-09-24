package com.megustav.lolesports.schedule.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A telegram bot providing information on the upcoming competitive League of Legends events
 *
 * @author MeGusav
 *         23.09.17 23:37
 */
public class LolEsportsScheduleBot extends TelegramLongPollingBot {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);

    /** Overall messages received */
    private final AtomicLong received = new AtomicLong(0);
    /** Bot Username */
    private final String botName;
    /** Bot token */
    private final String botToken;

    public LolEsportsScheduleBot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        received.incrementAndGet();
        if (! update.hasMessage()) {
            log.debug("No message received");
            return;
        }

        Message content = update.getMessage();
        log.debug("Received text: {}", content.getText());
        log.trace("Received data: {}", content);
        try {

            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText("I've had enough! Already " + received.get() + " pointless messages!");
            log.debug("Prepared response: {}", message);
            execute(message);
        } catch (Exception ex) {
            log.error("Error processing update", ex);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
