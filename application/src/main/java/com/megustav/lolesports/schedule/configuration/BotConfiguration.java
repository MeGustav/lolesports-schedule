package com.megustav.lolesports.schedule.configuration;

import com.megustav.lolesports.schedule.bot.BotRegistry;
import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.annotation.PostConstruct;

/**
 * Schedule bot configuration
 *
 * @author MeGusav
 *         14/11/2017 21:27
 */
@Configuration
public class BotConfiguration {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(BotConfiguration.class);

    /** Environment parameters */
    private final Environment env;

    @Autowired
    public BotConfiguration(Environment env) {
        this.env = env;
    }

    /**
     * Initialize bots
     */
    @PostConstruct
    public void initBots() {
        log.info("Initializing bots...");
        botRegistry().register(telegramBot());
    }

    @Bean
    public BotRegistry botRegistry() {
        return new BotRegistry();
    }

    @Bean
    public TelegramLongPollingBot telegramBot() {
        return new LolEsportsScheduleBot(env.getProperty("telegram.bot.name"), env.getProperty("telegram.bot.token"));
    }

}