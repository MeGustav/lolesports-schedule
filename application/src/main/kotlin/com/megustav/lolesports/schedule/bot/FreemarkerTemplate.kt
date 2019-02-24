package com.megustav.lolesports.schedule.bot

import com.megustav.lolesports.schedule.riot.League
import com.megustav.lolesports.schedule.riot.MatchInfo
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.StringWriter
import java.time.LocalDate

/**
 *
 * @author MeGustav
 *         2019-02-24 23:18
 */
class UpcomingTemplateEvaluator(configuration: Configuration): AbstractTemplateEvaluator(configuration) {
    override fun templateName(): String = "upcoming.ftl"
}

abstract class AbstractTemplateEvaluator(configuration: Configuration) {

    private val template: Template

    abstract fun templateName(): String

    init {
        template = configuration.getTemplate(templateName())
    }
    companion object {
        private const val LEAGUE_PARAM = "leagueName"
        private const val SCHEDULE_PARAM = "schedule"
    }

    fun formMessagePayload(league: League,
                           matches: Map<LocalDate, List<MatchInfo>>): String {
        val params = mapOf(LEAGUE_PARAM to league, SCHEDULE_PARAM to matches)
        StringWriter().use { out ->
            template.process(params, out)
            return out.toString()
        }
    }
}