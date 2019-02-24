package com.megustav.lolesports.schedule.impl

import com.megustav.lolesports.schedule.bot.UpcomingTemplateEvaluator
import com.megustav.lolesports.schedule.configuration.FreemarkerConfiguration
import com.megustav.lolesports.schedule.riot.League
import com.megustav.lolesports.schedule.riot.MatchInfo
import freemarker.template.Configuration
import no.api.freemarker.java8.Java8ObjectWrapper
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

/**
 * @author MeGustav
 *         2019-02-24 16:15
 */
@RunWith(MockitoJUnitRunner::class)
class TestUpcomingTemplateEvaluatorImpl {

    companion object {
        private val DEFAULT_LEAGUE = League.LEC
    }

    private val configuration = Configuration(Configuration.VERSION_2_3_23).apply {
        setClassForTemplateLoading(FreemarkerConfiguration::class.java, "/template/")
        defaultEncoding = StandardCharsets.UTF_8.name()
        objectWrapper = Java8ObjectWrapper(Configuration.getVersion())
    }

    private val evaluator = UpcomingTemplateEvaluator(configuration)

    @Test
    fun testUpcomingWithValidData() {
        val data = givenCompleteMatchesData()
        val message = whenFormingMessage(data)
        thenMessageIs(message, "valid-data-message.md")
    }

    private fun givenCompleteMatchesData(): Map<LocalDate, List<MatchInfo>> =
            mapOf(
                    LocalDate.of(2019, 1, 1) to listOf(
                            MatchInfo("id1", "Game1", setOf("Team1", "Team2"), LocalDateTime.of(2019, 1, 1, 1, 1, 1)),
                            MatchInfo("id2", "Game2", setOf("Team3", "Team4"), LocalDateTime.of(2019, 1, 1, 2, 0, 0))
                    ),
                    LocalDate.of(2019, 1, 2) to listOf(
                            MatchInfo("id3", "Game1", setOf("Team2", "Team3"), LocalDateTime.of(2019, 1, 2, 1, 1, 1)),
                            MatchInfo("id4", "Game2", setOf("Team1", "Team4"), LocalDateTime.of(2019, 1, 2, 2, 0, 0))
                    )
            )

    private fun whenFormingMessage(data: Map<LocalDate, List<MatchInfo>>): String =
            evaluator.formMessagePayload(DEFAULT_LEAGUE, data)

    private fun thenMessageIs(message: String, file: String) {
        assertEquals(readFile(file), message)
    }

    private fun readFile(file: String) =
            TestUpcomingTemplateEvaluatorImpl::class.java.getResource("/template/$file")
                    .readText(StandardCharsets.UTF_8)
}