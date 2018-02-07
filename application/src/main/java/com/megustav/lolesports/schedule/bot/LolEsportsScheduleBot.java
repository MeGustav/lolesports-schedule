package com.megustav.lolesports.schedule.bot;

import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.data.ScheduleInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * A telegram bot providing information on the upcoming competitive League of Legends events
 *
 * @author MeGustav
 *         23.09.17 23:37
 */
public class LolEsportsScheduleBot extends TelegramLongPollingBot {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);

    /** Bot Username */
    private final String botName;
    /** Bot token */
    private final String botToken;
    /** Riot API client */
    private final RiotApiClient apiClient;

    public LolEsportsScheduleBot(String botName, String botToken, RiotApiClient apiClient) {
        this.botName = botName;
        this.botToken = botToken;
        this.apiClient = apiClient;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (! update.hasMessage()) {
            log.debug("No message received");
            return;
        }

        Message content = update.getMessage();
        log.debug("Received text: {}", content.getText());
        log.trace("Received data: {}", content);
        try {
            ScheduleInformation response = apiClient.getSchedule(League.NALCS);
            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText("Scheduled items: " + response.getScheduleItems().size());
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

    @Override
    public String getBotToken() {
        return botToken;
    }
}
