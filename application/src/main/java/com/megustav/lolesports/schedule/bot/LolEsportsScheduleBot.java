package com.megustav.lolesports.schedule.bot;

import com.megustav.lolesports.schedule.processor.MessageProcessor;
import com.megustav.lolesports.schedule.processor.ProcessingInfo;
import com.megustav.lolesports.schedule.processor.ProcessorRepository;
import com.megustav.lolesports.schedule.processor.ProcessorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

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
    /** Bot registry */
    private final BotRegistry botRegistry;
    /** Processor repository */
    private final ProcessorRepository repository;
    /** Task executor */
    private final TaskExecutor executor;

    public LolEsportsScheduleBot(String botName,
                                 String botToken,
                                 BotRegistry botRegistry,
                                 TaskExecutor executor,
                                 ProcessorRepository repository) {
        this.botName = botName;
        this.botToken = botToken;
        this.botRegistry = botRegistry;
        this.executor = executor;
        this.repository = repository;
    }

    /**
     * Registering bot in the registry
     */
    public void init() {
        botRegistry.register(this);
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
        ProcessingInfo info = retrieveProcessingInfo(update);
        if (info == null) {
            log.error("Couldn't retrieve any processing information");
            return;
        }

        Long chatId = info.getChatId();
        String payload = info.getPayload();
        if (payload == null) {
            processBusinessError(chatId, "No payload received");
            return;
        }

        // Getting request type
        ProcessorType type = ProcessorType.fromRequest(payload);
        if (type == null) {
            processBusinessError(chatId, "Unsupported processor type: " + payload);
            return;
        }

        // Getting the actual processor
        MessageProcessor processor = repository.getProcessor(type);
        if (processor == null) {
            processBusinessError(chatId, "No processor registered for type: " + type);
            return;
        }

        // Sending response message
        try {
            execute(processor.processIncomingMessage(info));
        } catch (Exception ex) {
            log.error("{} Error processing message", MessageUtils.formChatPrefix(chatId), ex);
            execute(new SendMessage(chatId, "Internal server error"));
        }
    }

    /**
     * Retrieve processing information
     *
     * @param update update information
     * @return processing information
     */
    private ProcessingInfo retrieveProcessingInfo(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            return new ProcessingInfo(message.getChatId(), message.getText());
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return new ProcessingInfo(callbackQuery.getMessage().getChatId(), callbackQuery.getData());
        } else {
            return null;
        }
    }

    /**
     * Process an error situation
     *
     * @param chatId chat id
     * @param errorMessage error message
     */
    private void processBusinessError(Long chatId, String errorMessage) throws TelegramApiException {
        MessageUtils.consumeMessage(log::error, chatId, errorMessage);
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
