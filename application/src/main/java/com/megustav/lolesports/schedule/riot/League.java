package com.megustav.lolesports.schedule.riot;

/**
 * Leagues
 *
 * @author MeGustav
 *         25/01/2018 23:39
 */
public enum League {

    /** NA */
    NALCS(2),
    /** Europe */
    EULCS(3),
    /** Korea */
    LCK(6),
    /** China */
    LPL(7),
    /** Southeast Asia */
    LMS(8);

    /** League id */
    private final int id;

    League(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
