package com.megustav.lolesports.schedule.configuration;

import com.megustav.lolesports.schedule.bot.BotRegistry;
import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Schedule bot beans configuration
 *
 * @author MeGustav
 *         14/11/2017 21:27
 */
@Configuration
@Import(ProcessorConfiguration.class)
public class BotConfiguration {

    /** Environment parameters */
    private final Environment env;
    /** Processor configuration */
    private final ProcessorConfiguration processorConfiguration;

    @Autowired
    public BotConfiguration(Environment env,
                            ProcessorConfiguration processorConfiguration) {
        this.env = env;
        this.processorConfiguration = processorConfiguration;
    }

    /**
     * @return bot registry
     */
    @Bean
    public BotRegistry botRegistry() {
        return new BotRegistry();
    }

    /**
     * @return telegram bot
     */
    @Bean(initMethod = "init")
    public LolEsportsScheduleBot telegramBot() {
        return new LolEsportsScheduleBot(
                env.getProperty("telegram.bot.name"),
                env.getProperty("telegram.bot.token"),
                botRegistry(),
                taskExecutor(),
                processorConfiguration.processorRepository()
        );
    }

    /**
     * @return task executor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setThreadNamePrefix("processor");
        executor.setMaxPoolSize(10);
        return executor;
    }

}
