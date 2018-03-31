package com.megustav.lolesports.schedule.riot.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Matches information
 *
 * @author MeGustav
 *         25/01/2018 22:57
 */
public class Match {

    /** Match id */
    private final String id;
    /** Match name */
    private final String name;
    /** Rosters */
    private final List<MatchRoster> rosters;

    @JsonCreator
    public Match(@JsonProperty("id") String id,
                 @JsonProperty("name") String name,
                 @JsonProperty("input") List<MatchRoster> rosters) {
        this.id = id;
        this.rosters = rosters;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MatchRoster> getRosters() {
        return rosters;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", rosters=" + rosters +
                '}';
    }
}
