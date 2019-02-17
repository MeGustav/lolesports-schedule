package com.megustav.lolesports.schedule.riot

enum class League(val officialName: String,
                  val aliases: Set<String> = setOf()) {

    LCS("lcs", setOf("nalcs", "na", "america")),
    LEC("lec", setOf("eulcs", "eu", "europe")),
    LCK("lck", setOf("ogn", "korea")),
    LPL("lpl", setOf("china")),
    LMS("lms", setOf("garena"));

    companion object {

        @JvmStatic
        fun fromAlias(alias: String): League? =
                values().find { it.officialName == alias }
    }
}