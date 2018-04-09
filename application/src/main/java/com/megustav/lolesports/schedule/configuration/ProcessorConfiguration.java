package com.megustav.lolesports.schedule.configuration;

import com.megustav.lolesports.schedule.processor.ProcessorRepository;
import com.megustav.lolesports.schedule.processor.StartProcessor;
import com.megustav.lolesports.schedule.processor.UpcomingMatchesProcessor;
import com.megustav.lolesports.schedule.transformer.UpcomingMatchesTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

/**
 * Processors configuration
 *
 * @author MeGustav
 *         02/04/2018 17:12
 */
@Configuration
@Import({
        RiotApiConfiguration.class,
        FreemarkerConfiguration.class
})
public class ProcessorConfiguration {

    /** API configuration */
    private final RiotApiConfiguration apiConfiguration;
    /** Freemarker configuration */
    private final FreemarkerConfiguration freemarkerConfiguration;

    @Autowired
    public ProcessorConfiguration(RiotApiConfiguration apiConfiguration,
                                  FreemarkerConfiguration freemarkerConfiguration) {
        this.apiConfiguration = apiConfiguration;
        this.freemarkerConfiguration = freemarkerConfiguration;
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
    public UpcomingMatchesProcessor upcomingMatchesProcessor() throws IOException {
        return new UpcomingMatchesProcessor(
                apiConfiguration.riotApiClient(),
                processorRepository(),
                upcomingMatchesTransformer(),
                freemarkerConfiguration.configuration()
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
