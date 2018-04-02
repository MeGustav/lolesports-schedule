package com.megustav.lolesports.schedule.riot.data;

import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Match information
 *
 * @author MeGustav
 *         08/03/2018 22:46
 */
public class MatchInfo {

    /** Match id */
    private final String id;
    /** Match name */
    private final String name;
    /** Teams */
    private final Set<String> teams;
    /** Match schedule time */
    private final ZonedDateTime time;

    public MatchInfo(String id, String name, Set<String> teams, ZonedDateTime time) {
        this.id = id;
        this.name = name;
        this.teams = teams;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public Set<String> getTeams() {
        return teams;
    }
}

