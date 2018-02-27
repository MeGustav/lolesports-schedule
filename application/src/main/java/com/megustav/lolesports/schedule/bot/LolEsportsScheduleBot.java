package com.megustav.lolesports.schedule.bot;

import com.megustav.lolesports.schedule.processor.MessageProcessor;
import com.megustav.lolesports.schedule.processor.ProcessorRepository;
import com.megustav.lolesports.schedule.processor.ProcessorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
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
        if (! update.hasMessage()) {
            log.debug("No message received");
            return;
        }

        // Getting text message
        Message message = update.getMessage();
        String text = message.getText();
        if (text == null) {
            log.warn("No text received");
            return;
        }

        // Getting request type
        Optional<ProcessorType> typeOpt = ProcessorType.fromRequest(text);
        if (! typeOpt.isPresent()) {
            log.warn("Unsupported processor type: {}", text);
            return;
        }

        // Getting the actual processor
        Optional<MessageProcessor> processorOpt = repository.getProcessor(typeOpt.get());
        if (! processorOpt.isPresent()) {
            log.warn("No processor registered for type: ", typeOpt.get());
            return;
        }

        // Submitting task with bot callback
        executor.execute(() -> {
            try {
                execute(processorOpt.get().processIncomingMessage(message));
            } catch (TelegramApiException ex) {
                log.error("Error processing message: {}", text, ex);
            }
        });
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
