package com.megustav.lolesports.schedule.processor.impl.upcoming

import com.megustav.lolesports.schedule.riot.MatchInfo
import com.megustav.lolesports.schedule.riot.json.Match
import com.megustav.lolesports.schedule.riot.json.ScheduleInformation
import com.megustav.lolesports.schedule.riot.json.ScheduleItem
import com.megustav.lolesports.schedule.riot.json.TournamentInfo
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author MeGustav
 *         2019-02-23 12:58
 */
class UpcomingMatchesTransformer {

    companion object {
        private val log = LoggerFactory.getLogger(UpcomingMatchesTransformer::class.java)
        private const val MATCHES_LIMIT = 10
    }

    /**
     * Filtering the schedule and forming outgoing message
     * This as well is to be annihilated when persistence is implemented
     *
     * TODO persistent data and optimisation
     *
     * @param schedule schedule information
     * @return list of matches to be sent
     */
    fun transform(schedule: ScheduleInformation): Map<LocalDate, List<MatchInfo>> =
            retrieveMatches(schedule).groupByTo(mutableMapOf()) { match ->
                match.time.toLocalDate()
            }.toSortedMap().apply {
                values.forEach { matches ->
                    matches.sortBy { it.time }
                }
            }

    private fun retrieveMatches(schedule: ScheduleInformation): List<MatchInfo> {
        val matches = ArrayList<MatchInfo>()
        for (item in retrieveScheduleItems(schedule)) {
            val tournament = schedule.tournaments.find { it.id == item.tournament }
            if (tournament == null) {
                log.warn("Tournament ${item.tournament} was not found")
                continue
            }
            val match = findItemMatch(item, tournament)
            if (match == null) {
                log.warn("Match ${item.match} was not found")
                continue
            }

            matches.add(MatchInfo(
                    id = item.match!!,
                    name = match.name!!,
                    teams = retrieveTeams(match, tournament),
                    time = LocalDateTime.ofInstant(item.time!!.toInstant(), ZoneOffset.UTC)
            ))
        }
        return matches
    }

    private fun retrieveScheduleItems(schedule: ScheduleInformation): List<ScheduleItem> {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val dateFilter: (item: ScheduleItem) -> Boolean = {
            ZonedDateTime.ofInstant(it.time!!.toInstant(), ZoneOffset.UTC).isAfter(now)
        }
        return schedule.scheduleItems.asSequence()
                .filter { it.tournament != null }
                .filter { it.bracket != null }
                .filter { it.match != null }
                .filter { it.time != null }
                .filter { dateFilter(it) }
                .sortedBy { it.time }
                .take(MATCHES_LIMIT)
                .toList()
    }

    private fun findItemMatch(item: ScheduleItem, tournament: TournamentInfo): Match? =
            tournament.brackets
                    .map { it.value }
                    .filter { Objects.equals(item.bracket, it.id) }
                    .map { bracket -> bracket.matches }
                    .flatMap { it.entries }
                    .map { it.value }
                    .find { match -> Objects.equals(item.match, match.id) }

    private fun retrieveTeams(match: Match, tournament: TournamentInfo): Set<String> {
        val ids = match.rosters.map { it.id }.toSet()
        return tournament.rosters
                .map { it.value }
                .filter { ids.contains(it.id) }
                .mapNotNull { it.name }
                .toSet()
    }

}