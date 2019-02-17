package com.megustav.lolesports.schedule.processor

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot
import org.slf4j.LoggerFactory

/**
 * Processor repository
 */
class ProcessorRepository {

    companion object {
        /** Logger  */
        private val log = LoggerFactory.getLogger(LolEsportsScheduleBot::class.java)
    }

    /** Processors. As bot itself is singlethreaded there is no need in ConcurrentHashMap  */
    private val processors = mutableMapOf<ProcessorType, MessageProcessor>()

    /**
     * Register processor in repository
     *
     * @param processor processor
     */
    fun register(processor: MessageProcessor) {
        log.debug("Registering processor of type ${processor.type}")
        processors[processor.type] = processor
    }

    /**
     * Retrieve processor
     *
     * @param type processor type
     * @return processor
     */
    fun getProcessor(type: ProcessorType): MessageProcessor? = processors[type]

}
