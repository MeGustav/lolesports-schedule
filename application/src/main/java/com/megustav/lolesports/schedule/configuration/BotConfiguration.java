package com.megustav.lolesports.schedule.configuration;

import com.megustav.lolesports.schedule.bot.BotRegistry;
import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.processor.UpcomingMatchesProcessor;
import com.megustav.lolesports.schedule.processor.ProcessorRepository;
import com.megustav.lolesports.schedule.processor.StartProcessor;
import com.megustav.lolesports.schedule.riot.transformer.UpcomingMatchesTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;

/**
 * Schedule bot beans configuration
 *
 * @author MeGustav
 *         14/11/2017 21:27
 */
@Configuration
@Import(RiotApiConfiguration.class)
public class BotConfiguration {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(BotConfiguration.class);

    /** Environment parameters */
    private final Environment env;
    /** API configuration */
    private final RiotApiConfiguration apiConfiguration;

    @Autowired
    public BotConfiguration(Environment env, RiotApiConfiguration apiConfiguration) {
        this.env = env;
        this.apiConfiguration = apiConfiguration;
    }

    /**
     * Initialize bots
     */
    @PostConstruct
    public void initBots() {
        log.info("Initializing bots...");
        botRegistry().register(telegramBot());
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
    @Bean
    public LolEsportsScheduleBot telegramBot() {
        return new LolEsportsScheduleBot(
                env.getProperty("telegram.bot.name"),
                env.getProperty("telegram.bot.token"),
                taskExecutor(),
                processorRepository()
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

    /**
     * @return processor repository
     */
    @Bean
    public ProcessorRepository processorRepository() {
        return new ProcessorRepository();
    }

    /**
     * @return processor returning full schedule
     */
    @Bean(initMethod = "init")
    public UpcomingMatchesProcessor upcomingMatchesProcessor() {
        return new UpcomingMatchesProcessor(
                apiConfiguration.riotApiClient(),
                processorRepository(),
                upcomingMatchesTransformer()
        );
    }

    /**
     * @return interaction start processor
     */
    @Bean(initMethod = "init")
    public StartProcessor startProcessor() {
        return new StartProcessor(processorRepository());
    }

    /**
     * @return upcoming matches transformer
     */
    @Bean
    public UpcomingMatchesTransformer upcomingMatchesTransformer() {
        return new UpcomingMatchesTransformer();
    }


}
