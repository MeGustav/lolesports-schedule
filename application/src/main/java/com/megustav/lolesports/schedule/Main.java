package com.megustav.lolesports.schedule;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.Properties;

/**
 * Application entry point
 *
 * @author MeGusav
 *         23.09.17 20:38
 */
public class Main {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Application entry point
     */
    public static void main(String[] args) throws Exception {
        log.info("Starting bot...");
        log.debug("Reading configuration...");
        Properties properties = new Properties();
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(stream);
            log.debug("Configuration read");
        }
        try {
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            botsApi.registerBot(new LolEsportsScheduleBot(
                    properties.getProperty("telegram.bot.name"),
                    properties.getProperty("telegram.bot.token")));
        } catch (TelegramApiException ex) {
            log.error("Error instantiating bot", ex);
        }
    }

}
