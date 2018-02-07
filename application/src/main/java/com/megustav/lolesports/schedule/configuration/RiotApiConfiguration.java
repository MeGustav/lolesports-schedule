package com.megustav.lolesports.schedule.configuration;

import com.megustav.lolesports.schedule.riot.RiotApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Riot API beans configuration
 *
 * @author MeGustav
 *         25/01/2018 22:25
 */
@Configuration
public class RiotApiConfiguration {

    /**
     * @return Riot API client
     */
    @Bean
    public RiotApiClient riotApiClient() {
        return new RiotApiClient();
    }

}
