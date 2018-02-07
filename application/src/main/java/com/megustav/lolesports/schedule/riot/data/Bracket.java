package com.megustav.lolesports.schedule.riot.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Bracket information
 *
 * @author MeGustav
 *         25/01/2018 22:57
 */
public class Bracket {

    /** Bracket id */
    private final String id;
    /** Matches within the bracket */
    private final Map<String, Match> matches;

    @JsonCreator
    public Bracket(@JsonProperty("id") String id,
                   @JsonProperty("matches") Map<String, Match> matches) {
        this.id = id;
        this.matches = matches;
    }

    public String getId() {
        return id;
    }

    public Map<String, Match> getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        return "Bracket{" +
                "id='" + id + '\'' +
                ", matches=" + matches +
                '}';
    }
}
