package com.megustav.lolesports.schedule.riot.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Roster info represented in match information
 * (containing roster id at max)
 *
 * @author MeGustav
 *         31/03/2018 16:38
 */
public class MatchRoster {

    /** Roster Id */
    private final String id;

    @JsonCreator
    public MatchRoster(@JsonProperty("roster") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MatchRoster{" +
                "id='" + id + '\'' +
                '}';
    }
}
