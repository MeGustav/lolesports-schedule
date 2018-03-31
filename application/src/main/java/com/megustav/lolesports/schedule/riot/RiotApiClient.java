package com.megustav.lolesports.schedule.riot;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 * Riot API client
 *
 * @author MeGustav
 *         25/01/2018 22:14
 */
public class RiotApiClient {

    /** Riot api schedule url */
    private static final String SCHEDULE_URL =
            "https://api.lolesports.com/api/v1/scheduleItems";

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

        return ClientBuilder.newClient(new ClientConfig(provider));
    }
}
