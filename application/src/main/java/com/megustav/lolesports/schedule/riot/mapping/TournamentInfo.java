package com.megustav.lolesports.schedule.riot.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Tournament information
 *
 * @author MeGustav
 *         25/01/2018 22:57
 */
public class TournamentInfo {

    /** Tournament id */
    private final String id;
    /** Tournament description */
    private final String description;
    /** Tournament bracket */
    private final Map<String, Bracket> brackets;
    /** Rosters competing in the tournament */
    private final Map<String, Roster> rosters;

    @JsonCreator
    public TournamentInfo(@JsonProperty("id") String id,
                          @JsonProperty("description") String description,
                          @JsonProperty("brackets") Map<String, Bracket> brackets,
                          @JsonProperty("rosters") Map<String, Roster> rosters) {
        this.id = id;
        this.description = description;
        this.brackets = brackets;
        this.rosters = rosters;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Bracket> getBrackets() {
        return brackets;
    }

    public Map<String, Roster> getRosters() {
        return rosters;
    }

    @Override
    public String toString() {
        return "TournamentInfo{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", brackets=" + brackets +
                ", rosters=" + rosters +
                '}';
    }
}
