package com.megustav.lolesports.schedule.requester;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.data.UpcomingMatches;
import com.megustav.lolesports.schedule.processor.upcoming.UpcomingMatchesTransformer;
import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Requests upcoming matches through Riot API
 *
 * @author MeGustav
 *         20/04/2018 00:45
 */
public class UpcomingMatchesApiRequester
        implements DataRequester<League, UpcomingMatches> {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);

    /** Riot api */
    private final RiotApiClient apiClient;
    /** Upcoming matches transformer */
    private final UpcomingMatchesTransformer transformer;

    public UpcomingMatchesApiRequester(RiotApiClient apiClient,
                                       UpcomingMatchesTransformer transformer) {
        this.apiClient = apiClient;
        this.transformer = transformer;
    }

    @Override
    public UpcomingMatches requestData(League request) {
        log.debug("Receiving the upcoming matches for: {}", request);
        ScheduleInformation schedule = apiClient.getSchedule(request);
        // Transforming data into convenient form
        return new UpcomingMatches(transformer.transform(schedule));
    }
}
