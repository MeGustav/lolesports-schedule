package com.megustav.lolesports.schedule.bot;

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
public class Bot extends TelegramLongPollingBot {

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
            System.out.println("No message received");
            return;
        }
        Message received = update.getMessage();
        System.out.println("[====]");
        System.out.println("Message received");
        System.out.println("User: " + received.getFrom().getFirstName() + " " + received.getFrom().getLastName());
        System.out.println("Content: " + received.getText());
        System.out.println("[====]");
        try {

            SendMessage message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText(received.getText().contains("Инн") ?
                            "Люблю тебя. Дай еще супа" :
                            "Not implemented yet");
            sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
}
