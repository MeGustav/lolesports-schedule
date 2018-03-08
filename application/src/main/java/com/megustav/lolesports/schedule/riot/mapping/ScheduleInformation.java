package com.megustav.lolesports.schedule.riot.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Schedule information
 *
 * @author MeGustav
 *         25/01/2018 22:56
 */
public class ScheduleInformation {

    /** Schedule information without deeper info */
    private final List<ScheduleItem> scheduleItems;
    /** Tournaments info */
    private final List<TournamentInfo> tournaments;

    @JsonCreator
    public ScheduleInformation(@JsonProperty("scheduleItems") List<ScheduleItem> scheduleItems,
                               @JsonProperty("highlanderTournaments") List<TournamentInfo> tournaments) {
        this.scheduleItems = scheduleItems;
        this.tournaments = tournaments;
    }

    public List<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }

    public List<TournamentInfo> getTournaments() {
        return tournaments;
    }

    @Override
    public String toString() {
        return "ScheduleInformation{" +
                "scheduleItems=" + scheduleItems +
                ", tournaments=" + tournaments +
                '}';
    }
}
