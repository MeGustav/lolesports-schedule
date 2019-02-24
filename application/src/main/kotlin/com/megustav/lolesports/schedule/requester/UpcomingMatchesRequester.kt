package com.megustav.lolesports.schedule.requester

import com.megustav.lolesports.schedule.processor.UpcomingMatches
import com.megustav.lolesports.schedule.processor.impl.upcoming.UpcomingMatchesTransformer
import com.megustav.lolesports.schedule.riot.League
import com.megustav.lolesports.schedule.riot.ScheduleApiClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Requests upcoming matches through Riot API
 */
class UpcomingMatchesRequester(
        /** Schedule API client */
        private val apiClient: ScheduleApiClient,
        /** Data transformer */
        private val dataTransformer: UpcomingMatchesTransformer
): DataRequester<League, UpcomingMatches> {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UpcomingMatchesRequester::class.java)
    }

    override fun requestData(request: League): UpcomingMatches {
        log.debug("Receiving the upcoming matches for: $request")
        return UpcomingMatches(
                dataTransformer.transform(apiClient.getSchedule(request))
        )
    }
}