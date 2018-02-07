package com.megustav.lolesports.schedule.riot.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Schedule item
 *
 * @author MeGustav
 *         25/01/2018 22:57
 */
public class ScheduleItem {

    /** Tournament GUID */
    private final String tournament;
    /** Bracket GUID */
    private final String bracket;
    /** Match GUID */
    private final String match;
    /** Schedule time */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final Date time;

    @JsonCreator
    public ScheduleItem(@JsonProperty("tournament") String tournament,
                        @JsonProperty("bracket") String bracket,
                        @JsonProperty("match") String match,
                        @JsonProperty("scheduledTime") Date time) {
        this.tournament = tournament;
        this.bracket = bracket;
        this.match = match;
        this.time = time;
    }

    public String getTournament() {
        return tournament;
    }

    public String getBracket() {
        return bracket;
    }

    public String getMatch() {
        return match;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "ScheduleItem{" +
                "tournament='" + tournament + '\'' +
                ", bracket='" + bracket + '\'' +
                ", match='" + match + '\'' +
                ", time=" + time +
                '}';
    }
}
