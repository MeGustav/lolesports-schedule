package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.data.ScheduleInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Class for full schedule requests processing
 *
 * @author MeGustav
 *         15/02/2018 22:09
 */
public class FullScheduleProcessor implements MessageProcessor {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);
    /** Riot api */
    private final RiotApiClient apiClient;
    /** Processor repository */
    private final ProcessorRepository repository;

    public FullScheduleProcessor(RiotApiClient apiClient, ProcessorRepository repository) {
        this.apiClient = apiClient;
        this.repository = repository;
    }

    /**
     * Initialization
     */
    public void init() {
        repository.register(this);
    }

    /**
     * Process incoming message
     *
     * @param incomingMessage incoming message
     */
    @Override
    public BotApiMethod<Message> processIncomingMessage(Message incomingMessage) {
        log.debug("Processing a full schedule request...");
        log.trace("Received data: {}", incomingMessage);
        ScheduleInformation schedule = apiClient.getSchedule(League.NALCS);
        SendMessage response = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText("Scheduled items: " + schedule.getScheduleItems().size());
        log.debug("Prepared response: {}", response);
        return response;
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.FULL_SCHEDULE;
    }
}
