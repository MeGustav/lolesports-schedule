package com.megustav.lolesports.schedule.processor

import com.megustav.lolesports.schedule.riot.MatchInfo
import java.time.LocalDate

/**
 * Processor type
 */
enum class ProcessorType(val path: String) {

    /** Bot interaction start  */
    START("/start"),
    /** Full schedule on an upcoming/ongoing week  */
    UPCOMING("/upcoming");

    companion object {
        @JvmStatic
        fun fromRequest(request: String): ProcessorType? =
                values().find { request.startsWith(it.path) }
    }
}

/**
 * Basic info for message processing
 */
data class ProcessingInfo(
        /** Chat id  */
        val chatId: Long,
        /** Message payload  */
        val payload: String? = null
)

data class UpcomingMatches(val matches: Map<LocalDate, List<MatchInfo>>)