package com.megustav.lolesports.schedule.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.LongPollingBot;

/**
 * Bean for bot initialization
 *
 * @author MeGustav
 *         14/11/2017 22:15
 */
public class BotRegistry {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(BotRegistry.class);

    /** Bots api */
    private final TelegramBotsApi api;

    public BotRegistry() {
        ApiContextInitializer.init();
        this.api = new TelegramBotsApi();
    }

    /**
     * Register bot in api
     *
     * @param bot bot implementation
     */
    public void register(LongPollingBot bot) {
        try {
            log.info("Registering bot...");
            api.registerBot(bot);
            log.info("Bot registered");
        } catch (TelegramApiException ex) {
            log.error("Error registering bot", ex);
        }
    }

}
