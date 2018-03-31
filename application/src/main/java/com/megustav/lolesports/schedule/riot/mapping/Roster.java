package com.megustav.lolesports.schedule.riot.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Roster information represented in tournament information
 *
 * @author MeGustav
 *         31/03/2018 16:07
 */
public class Roster {

    /** Roster id */
    private final String id;
    /** Roster (team) name */
    private final String name;

    @JsonCreator
    public Roster(@JsonProperty("id") String id,
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
        return "Roster{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
