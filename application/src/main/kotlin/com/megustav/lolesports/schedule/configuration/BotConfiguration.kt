package com.megustav.lolesports.schedule.configuration

import com.megustav.lolesports.schedule.bot.BotRegistry
import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

/**
 * @author MeGustav
 *         2019-02-24 13:11
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "bot")
open class BotProperties {
    lateinit var name: String
    lateinit var token: String
}

@Configuration
@Import(ProcessorConfiguration::class)
open class BotConfiguration @Autowired constructor(
        private val properties: BotProperties,
        private val processorConfiguration: ProcessorConfiguration
) {

    @Bean
    open fun botRegistry(): BotRegistry {
        return BotRegistry()
    }

    @Bean(initMethod = "init")
    open fun telegramBot(): LolEsportsScheduleBot {
        return LolEsportsScheduleBot(
                properties.name,
                properties.token,
                botRegistry(),
                taskExecutor(),
                processorConfiguration.processorRepository()
        )
    }

    @Bean
    open fun taskExecutor(): TaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 5
            threadNamePrefix = "processor"
            maxPoolSize = 10
        }
    }

}