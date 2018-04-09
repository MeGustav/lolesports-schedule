package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.riot.League;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class for a start request processing
 *
 * @author MeGustav
 *         15/02/2018 22:09
 */
public class StartProcessor implements MessageProcessor {

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
        List<InlineKeyboardButton> buttons = Stream.of(League.values())
                .map(league -> {
                    String data = ProcessorType.UPCOMING.getPath() + " " + league.getOfficialName();
                    String caption = league.getOfficialName().toUpperCase();
                    return new InlineKeyboardButton(caption).setCallbackData(data);
                })
                .collect(Collectors.toList());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup()
                .setKeyboard(Collections.singletonList(buttons));
        return new SendMessage()
                .setChatId(processingInfo.getChatId())
                .setText("Upcoming matches for:")
                .setReplyMarkup(markup);
    }
}
