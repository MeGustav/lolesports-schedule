package com.megustav.lolesports.schedule.riot.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonCreator
    public Match(@JsonProperty("id") String id,
                 @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
