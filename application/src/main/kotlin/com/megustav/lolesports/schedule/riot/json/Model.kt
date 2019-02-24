package com.megustav.lolesports.schedule.riot.json

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Schedule information
 */
data class ScheduleInformation @JsonCreator constructor(
        /** Schedule information without deeper info */
        @JsonProperty("scheduleItems") val scheduleItems: List<ScheduleItem>,
        /** Tournaments info */
        @JsonProperty("highlanderTournaments") val tournaments: List<TournamentInfo>
)

/**
 * Tournament information
 */
data class TournamentInfo @JsonCreator constructor(
        /** Tournament id */
        @JsonProperty("id") val id: String?,
        /** Tournament description */
        @JsonProperty("description") val description: String?,
        /** Tournament bracket */
        @JsonProperty("brackets") val brackets: Map<String, Bracket> = emptyMap(),
        /** Rosters competing in the tournament */
        @JsonProperty("rosters") val rosters: Map<String, Roster> = emptyMap()
)

/**
 * Schedule item
 */
data class ScheduleItem @JsonCreator constructor(
        /** Tournament GUID */
        @JsonProperty("tournament") val tournament: String?,
        /** Bracket GUID */
        @JsonProperty("bracket") val bracket: String?,
        /** Match GUID */
        @JsonProperty("match") val match: String?,

        /** Schedule time */
        @JsonProperty("scheduledTime")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val time: Date?
)

/**
 * Bracket information
 */
data class Bracket @JsonCreator constructor(
        /** Bracket id */
        @JsonProperty("id") val id: String?,
        /** Matches within the bracket */
        @JsonProperty("matches") val matches: Map<String, Match> = emptyMap()
)

/**
 * Match information
 */
data class Match @JsonCreator constructor(
        /** Match id */
        @JsonProperty("id") val id: String?,
        /** Match name */
        @JsonProperty("name") val name: String?,
        /** Rosters */
        @JsonProperty(value = "input", required = false) val rosters: List<MatchRoster> = listOf()
)

/**
 * Roster info represented in match information
 * (containing roster id at max)
 */
data class MatchRoster @JsonCreator constructor(
        /** Roster id */
        @JsonProperty("roster") val id: String?
)


/**
 * Roster information represented in tournament information
 */
data class Roster @JsonCreator constructor(
        /** Roster id */
        @JsonProperty("id") val id: String?,
        /** Roster name */
        @JsonProperty("name") val name: String?
)