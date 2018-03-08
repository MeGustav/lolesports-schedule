package com.megustav.lolesports.schedule.bot;

import com.megustav.lolesports.schedule.processor.MessageProcessor;
import com.megustav.lolesports.schedule.processor.ProcessorRepository;
import com.megustav.lolesports.schedule.processor.ProcessorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Optional;

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
    /** Processor repository */
    private final ProcessorRepository repository;
    /** Task executor */
    private final TaskExecutor executor;

    public LolEsportsScheduleBot(String botName,
                                 String botToken,
                                 TaskExecutor executor,
                                 ProcessorRepository repository) {
        this.botName = botName;
        this.botToken = botToken;
        this.executor = executor;
        this.repository = repository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Submitting task with bot callback
        executor.execute(() -> {
            try {
                processUpdate(update);
            } catch (TelegramApiException ex) {
                log.error("Error processing an update: {}", update, ex);
            }

        });
    }

    /**
     * Process an update
     *
     * @param update update
     */
    private void processUpdate(Update update) throws TelegramApiException {
        if (!update.hasMessage()) {
            log.debug("No message received");
            return;
        }

        // Getting text message
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();
        if (text == null) {
            processError(chatId, "No text received");
            return;
        }

        // Getting request type
        Optional<ProcessorType> typeOpt = ProcessorType.fromRequest(text);
        if (!typeOpt.isPresent()) {
            processError(chatId, "Unsupported processor type: " + text);
            return;
        }

        // Getting the actual processor
        Optional<MessageProcessor> processorOpt = repository.getProcessor(typeOpt.get());
        if (!processorOpt.isPresent()) {
            processError(chatId, "No processor registered for type: " + typeOpt.get());
            return;
        }

        // Sending response message
        execute(processorOpt.get().processIncomingMessage(message));
    }

    /**
     * Process an error situation
     *
     * @param chatId chat id
     * @param errorMessage error message
     */
    private void processError(Long chatId, String errorMessage) throws TelegramApiException {
        MessageUtils.consumeMessage(log::debug, chatId, errorMessage);
        execute(new SendMessage(chatId, errorMessage));
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
