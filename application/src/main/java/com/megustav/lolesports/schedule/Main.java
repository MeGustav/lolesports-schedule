package com.megustav.lolesports.schedule;

import com.megustav.lolesports.schedule.bot.Bot;
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

    public static void main(String[] args) {
        System.out.println("Starting application");
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
