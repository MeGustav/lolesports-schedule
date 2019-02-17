package com.megustav.lolesports.schedule.configuration;

import com.megustav.lolesports.schedule.requester.UpcomingMatchesRequester;
import com.megustav.lolesports.schedule.data.UpcomingMatches;
import com.megustav.lolesports.schedule.requester.CachingDataRequester;
import com.megustav.lolesports.schedule.processor.ProcessorRepository;
import com.megustav.lolesports.schedule.processor.impl.StartProcessor;
import com.megustav.lolesports.schedule.requester.DataRequester;
import com.megustav.lolesports.schedule.processor.upcoming.UpcomingMatchesProcessor;
import com.megustav.lolesports.schedule.processor.upcoming.UpcomingMatchesTransformer;
import com.megustav.lolesports.schedule.riot.League;
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
        CacheConfiguration.class,
        FreemarkerConfiguration.class
})
public class ProcessorConfiguration {

    /** API configuration */
    private final RiotApiConfiguration apiConfiguration;
    /** Cache configuration */
    private final CacheConfiguration cacheConfiguration;
    /** Freemarker configuration */
    private final FreemarkerConfiguration freemarkerConfiguration;

    @Autowired
    public ProcessorConfiguration(RiotApiConfiguration apiConfiguration,
                                  CacheConfiguration cacheConfiguration,
                                  FreemarkerConfiguration freemarkerConfiguration) {
        this.apiConfiguration = apiConfiguration;
        this.cacheConfiguration = cacheConfiguration;
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
                cachingUpcomingRequester(),
                processorRepository(),
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

    /**
     * @return caching upcoming matches requester
     */
    @Bean
    public DataRequester<League, UpcomingMatches> cachingUpcomingRequester() {
        return new CachingDataRequester<>(
                "UPCOMING",
                cacheConfiguration.cacheManager(),
                League.class,
                UpcomingMatches.class,
                // For now using the requester
                // which gets data using Riot API.
                // Later this could be swapped with, say,
                // database data requester
                riotRequester()
        );
    }

    /**
     * @return requester that gets data uning Riot API
     */
    @Bean
    public DataRequester<League, UpcomingMatches> riotRequester() {
        return new UpcomingMatchesRequester(apiConfiguration.riotApiClient(), upcomingMatchesTransformer());
    }

}
