package com.megustav.lolesports.schedule.riot.data;

import java.time.LocalDateTime;

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
    /** Match schedule time */
    private final LocalDateTime time;

    public MatchInfo(String id, String name, LocalDateTime time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getTime() {
        return time;
    }
}

