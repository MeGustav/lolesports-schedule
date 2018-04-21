package com.megustav.lolesports.schedule.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.megustav.lolesports.schedule.processor.upcoming.UpcomingMatchesTransformer;
import com.megustav.lolesports.schedule.riot.data.MatchInfo;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


/**
 * Upcoming matches transformer test.
 * No need in Spring environment
 *
 * @author MeGustav
 *         06/04/2018 16:11
 */
public class TestUpcomingMatchesTransformer {

    /** Transformer */
    private final UpcomingMatchesTransformer transformer = new UpcomingMatchesTransformer();
    /** JSON mapper */
    private static final ObjectReader READER = new ObjectMapper()
            .readerFor(ScheduleInformation.class);

    @Test
    public void base() throws IOException {
        ScheduleInformation schedule = READER.readValue(
                UpcomingMatchesTransformer.class.getResource("/upcoming/base-riot-response.json"));
        Map<LocalDate, List<MatchInfo>> transform = transformer.transform(schedule);
        Assertions.assertThat(transform.size()).as("Matches count").isEqualTo(2);
    }

}
