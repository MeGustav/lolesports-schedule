package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.riot.League;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class for a start request processing
 *
 * @author MeGustav
 *         15/02/2018 22:09
 */
public class StartProcessor implements MessageProcessor {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);
    /** Processor repository */
    private final ProcessorRepository repository;

    public StartProcessor(ProcessorRepository repository) {
        this.repository = repository;
    }

    /**
     * Initialization
     */
    public void init() {
        repository.register(this);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.START;
    }

    @Override
    public BotApiMethod<Message> processIncomingMessage(ProcessingInfo processingInfo) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup()
                .setKeyboard(Collections.singletonList(
                        Stream.of(League.values())
                                .map(league -> {
                                    String data = ProcessorType.FULL_SCHEDULE.getPath() + " " + league.getOfficialName();
                                    String caption  = league.getOfficialName().toUpperCase();
                                    return new InlineKeyboardButton(caption).setCallbackData(data);
                                })
                                .collect(Collectors.toList())
                        ));
        return new SendMessage()
                .setChatId(processingInfo.getChatId())
                .setText("Full schedule for:").setReplyMarkup(markup)
                .setReplyMarkup(markup);
    }
}
