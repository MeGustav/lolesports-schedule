package com.megustav.lolesports.schedule.riot

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.megustav.lolesports.schedule.riot.League.*
import com.megustav.lolesports.schedule.riot.json.ScheduleInformation
import org.glassfish.jersey.client.ClientConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType

class ScheduleApiClient {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ScheduleApiClient::class.java)

        /** Riot api schedule url */
        private const val SCHEDULE_URL = "https://api.lolesports.com/api/v1/scheduleItems"
        /** Timeouts  */
        private const val CONNECTION_TIMEOUT: Long = 30
        private const val READ_TIMEOUT: Long = 30

        private val LEAGUE_IDS = mapOf(
                LCS to 2, LEC to 3,
                LCK to 6, LPL to 7,
                LMS to 8
        )
    }

    private val client: Client = createClient()

    /**
     * Get schedule information for the league
     *
     * Although latest Riot API is labeled v3
     * we are forced to use this call
     * because latest api does not (at least officially) provide
     * any easy way to get info about the competitive scene.
     *
     * @param league league to fetch schedule for
     * @return schedule for the specified league
     */
    fun getSchedule(league: League): ScheduleInformation {
        log.info("Fetching schedule for $league from Riot API")
        return client.target(SCHEDULE_URL)
                .queryParam("leagueId", LEAGUE_IDS[league])
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet().invoke(ScheduleInformation::class.java)

    }

    private fun createClient(): Client {
        // Customizing mapper to ignore unknown properties
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val provider = JacksonJaxbJsonProvider().apply { setMapper(mapper) }

        return ClientBuilder.newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .withConfig(ClientConfig(provider))
                .build()
    }

}