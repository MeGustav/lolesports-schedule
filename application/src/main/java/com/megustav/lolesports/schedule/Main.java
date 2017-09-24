package com.megustav.lolesports.schedule;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Application entry point
 *
 * @author MeGusav
 *         23.09.17 20:38
 */
public class Main {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting bot...");
        try {
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            botsApi.registerBot(new LolEsportsScheduleBot());
        } catch (TelegramApiException ex) {
            log.error("Error instantiating bot");
        }
    }

}
