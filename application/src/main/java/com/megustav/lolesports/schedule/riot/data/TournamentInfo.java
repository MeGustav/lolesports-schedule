package com.megustav.lolesports.schedule.riot.data;

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

    @JsonCreator
    public TournamentInfo(@JsonProperty("id") String id,
                          @JsonProperty("description") String description,
                          @JsonProperty("brackets") Map<String, Bracket> brackets) {
        this.id = id;
        this.description = description;
        this.brackets = brackets;
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

    @Override
    public String toString() {
        return "TournamentInfo{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", brackets=" + brackets +
                '}';
    }
}
