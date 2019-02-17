package com.megustav.lolesports.schedule.riot

import java.time.LocalDateTime

data class MatchInfo(
        /** Match id */
        val id: String,
        /** Match name */
        val name: String,
        /**
         * Teams
         * TODO probably shouldn't be a set, just a couple of teams
         */
        val teams: Set<String>,
        /** Match schedule time */
        val time: LocalDateTime
)

