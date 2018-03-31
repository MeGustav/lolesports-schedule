package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Processor repository
 *
 * @author MeGustav
 *         15/02/2018 22:33
 */
public class ProcessorRepository {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);

    /** Processors. As bot itself is singlethreaded there is no need in ConcurrentHashMap */
    private final Map<ProcessorType, MessageProcessor> processors = new HashMap<>();

    /**
     * Register processor in repository
     *
     * @param processor processor
     */
    void register(MessageProcessor processor) {
        log.debug("Registering processor of type {}", processor.getType());
        processors.put(processor.getType(), processor);
    }

    /**
     * Retrieve processor
     *
     * @param type processor type
     * @return processor
     */
    public Optional<MessageProcessor> getProcessor(ProcessorType type) {
        return Optional.ofNullable(processors.get(type));
    }


}
