package com.megustav.lolesports.schedule.riot.transformer;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.riot.data.MatchInfo;
import com.megustav.lolesports.schedule.riot.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Upcoming matches transformer
 *
 * @author MeGustav
 *         31/03/2018 16:10
 */
public class UpcomingMatchesTransformer {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);
    /** Shown matches limit */
    private static final int MATCHES_LIMIT = 10;

    /**
     * Filtering the schedule and forming outgoing message
     * This as well is to be annihilated when persistence is implemented
     * TODO persistent data
     *
     * @param schedule schedule information
     * @return list of matches to be sent
     */
    public Map<LocalDate, List<MatchInfo>> transform(ScheduleInformation schedule) {
        // Retrieving matches from schedule
        List<MatchInfo> matches = retrieveMatches(schedule);
        // Sorting everything
        Comparator<MatchInfo> comparator = Comparator.comparing(MatchInfo::getTime);
        Map<LocalDate, List<MatchInfo>> result = matches.stream()
                .collect(Collectors.groupingBy(
                        match -> match.getTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.toList())
                );
        // Sorting like that instead of comparingAndThen because it's more readable this way
        result.values().forEach(list -> list.sort(comparator));
        return result;
    }

    /**
     * Retrieve matches info from {@link ScheduleInformation}
     *
     * @param schedule schedule information
     * @return matches info
     */
    private List<MatchInfo> retrieveMatches(ScheduleInformation schedule) {
        List<MatchInfo> matches = new ArrayList<>();
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<ScheduleItem> items = schedule.getScheduleItems().stream()
                .filter(item -> item.getTime().after(today))
                // Filtering out invalid data
                .filter(item -> Objects.nonNull(item.getTournament()))
                .filter(item -> Objects.nonNull(item.getBracket()))
                .filter(item -> Objects.nonNull(item.getMatch()))
                .limit(MATCHES_LIMIT)
                .collect(Collectors.toList());
        for (ScheduleItem item : items) {
            Optional<TournamentInfo> tournamentOpt = schedule.getTournaments().stream()
                    .filter(tournament -> tournament.getId().equals(item.getTournament()))
                    .findAny();
            if (! tournamentOpt.isPresent()) {
                log.warn("Tournament {} was not found", item.getTournament());
                continue;
            }

            // Forming main match information
            TournamentInfo tournament = tournamentOpt.get();
            Optional<Match> matchInfo = tournament.getBrackets().entrySet().stream()
                    .map(Map.Entry::getValue)
                    .filter(bracket -> Objects.equals(item.getBracket(), bracket.getId()))
                    .map(Bracket::getMatches)
                    .flatMap(map -> map.entrySet().stream())
                    .map(Map.Entry::getValue)
                    .filter(match -> Objects.equals(item.getMatch(), match.getId()))
                    .findAny();
            if (! matchInfo.isPresent()) {
                log.warn("Match {} was not found", item.getMatch());
                continue;
            }
            Match match = matchInfo.get();
            matches.add(new MatchInfo(
                    item.getMatch(),
                    match.getName(),
                    retrieveTeams(match, tournament),
                    ZonedDateTime.ofInstant(item.getTime().toInstant(), ZoneId.systemDefault())
            ));
        }
        return matches;
    }

    /**
     * Retrieve team names from roster ids
     *
     * @param match match info
     * @param tournament tournament info
     * @return team names
     */
    private Set<String> retrieveTeams(Match match, TournamentInfo tournament) {
        // Rosters info could be absent
        if (match.getRosters() == null) {
            return Collections.emptySet();
        }
        Set<String> rosterIds = match.getRosters().stream()
                .map(MatchRoster::getId)
                .collect(Collectors.toSet());
        return tournament.getRosters().entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(roster -> rosterIds.contains(roster.getId()))
                .map(Roster::getName)
                .collect(Collectors.toSet());
    }

}
