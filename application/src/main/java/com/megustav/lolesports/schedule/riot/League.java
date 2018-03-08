package com.megustav.lolesports.schedule.riot;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Leagues
 *
 * @author MeGustav
 *         25/01/2018 23:39
 */
public enum League {

    /** NA */
    NALCS(2, "na", "nalcs", "america"),
    /** Europe */
    EULCS(3, "eu", "eulcs", "europe"),
    /** Korea */
    LCK(6, "lck", "ogn", "korea"),
    /** China */
    LPL(7, "lpl", "china"),
    /** Southeast Asia */
    LMS(8, "lms", "garena");

    /** League id */
    private final int id;
    /** League known aliases */
    private final Set<String> aliases;
    
    League(int id, String... aliases) {
        this.id = id;
        this.aliases = Stream.of(aliases).collect(Collectors.toSet());
    }

    public int getId() {
        return id;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Get a {@link League} instance by alias
     *
     * @param alias alias
     * @return {@link League}
     */
    public static Optional<League> fromAlias(String alias) {
        return Stream.of(values())
                .filter(league -> league.getAliases().contains(alias))
                .findAny();
    }
}
