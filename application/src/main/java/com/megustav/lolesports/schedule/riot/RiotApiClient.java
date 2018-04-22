package com.megustav.lolesports.schedule.riot;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;

/**
 * Riot API client
 *
 * @author MeGustav
 *         25/01/2018 22:14
 */
public class RiotApiClient {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);
    /** Riot api schedule url */
    private static final String SCHEDULE_URL =
            "https://api.lolesports.com/api/v1/scheduleItems";
    /** Timeouts */
    private static final long CONNECTION_TIMEOUT = 30;
    private static final long READ_TIMEOUT = 30;

    /** HTTP-client */
    private final Client client;

    public RiotApiClient() {
        this.client = createClient();
    }

    /**
     * Get schedule information for the league
     *
     * Although latest Riot API is labeled v3
     * we are forced (as are LoLEsports) to use this obsolete call
     * because latest api does not (at least officially) provide
     * any easy way to get info about the competitive scene.
     *
     * @param league league to fetch schedule for
     * @return schedule for the specified league
     */
    public ScheduleInformation getSchedule(League league) {
        log.debug("Fetching schedule for {} from Riot API", league);
        return client.target(SCHEDULE_URL)
                .queryParam("leagueId", league.getId())
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet().invoke(ScheduleInformation.class);

    }

    /**
     * @return configured client
     */
    private Client createClient() {
        // Customizing mapper to ignore unknown properties
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);

        return ClientBuilder.newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .withConfig(new ClientConfig(provider))
                .build();
    }
}
