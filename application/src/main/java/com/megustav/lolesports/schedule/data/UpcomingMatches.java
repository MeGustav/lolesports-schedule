package com.megustav.lolesports.schedule.data;

import com.megustav.lolesports.schedule.riot.MatchInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Class representing the upcoming matches
 *
 * @author MeGustav
 *         20/04/2018 00:51
 */
public class UpcomingMatches {

    /** Matches */
    private final Map<LocalDate, List<MatchInfo>> matches;

    public UpcomingMatches(Map<LocalDate, List<MatchInfo>> matches) {
        this.matches = matches;
    }

    public Map<LocalDate, List<MatchInfo>> getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        return "UpcomingMatches{" +
                "matches=" + matches +
                '}';
    }
}
