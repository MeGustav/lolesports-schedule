package com.megustav.lolesports.schedule.impl

import com.megustav.lolesports.schedule.data.UpcomingMatches
import com.megustav.lolesports.schedule.processor.ProcessingInfo
import com.megustav.lolesports.schedule.processor.ProcessorRepository
import com.megustav.lolesports.schedule.processor.ProcessorType
import com.megustav.lolesports.schedule.processor.upcoming.UpcomingMatchesProcessor
import com.megustav.lolesports.schedule.requester.DataRequester
import com.megustav.lolesports.schedule.riot.League
import com.megustav.lolesports.schedule.riot.MatchInfo
import freemarker.template.Configuration
import freemarker.template.Template
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.verification.VerificationMode
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import java.io.Writer
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

/**
 * @author MeGustav
 *         2019-02-14 20:55
 */
@RunWith(MockitoJUnitRunner::class)
class TestUpcomingMatchesProcessorImpl {

    companion object {
        private const val UPCOMING_COMMAND = "/upcoming"
        private val DEFAULT_LEAGUE = League.LEC
    }

    @Mock
    private lateinit var dataRequester: DataRequester<League, UpcomingMatches>

    @Mock
    private lateinit var repository: ProcessorRepository

    @Mock
    private lateinit var freemarkerConfiguration: Configuration

    @Mock
    private lateinit var messageTemplate: Template

    private lateinit var processor: UpcomingMatchesProcessor

    @Before
    fun init() {
        `when`(freemarkerConfiguration.getTemplate(eq("upcoming.ftl"))).thenReturn(messageTemplate)
        processor = UpcomingMatchesProcessor(
                dataRequester, repository, freemarkerConfiguration
        )
    }

    @Test
    fun testScheduleUpcomingProcessor() {
        val incoming = givenValidIncomingMessage()
        val text = givenMessageTemplateProcesses()
        val matches = givenMatchesFound()

        val sent = whenRequestingMatches(incoming)

        thenDataRequesterWasInvoked()
        thenMessageTemplateWasInvoked(matches)
        thenOutgoingMessageIsComplete(sent, text)
    }

    @Test
    fun testScheduleUpcomingProcessorInvalidMessageFormat() {
        val incoming = givenInvalidIncomingMessageFormat()
        val matches = givenMatchesFound()
        val sent = whenRequestingMatches(incoming)

        thenDataRequesterWasNotInvoked()
        thenMessageTemplateWasNotInvoked(matches)
        thenOutgoingTextIsCorrect(sent, "Unexpected message: '${incoming.payload}'")
    }

    @Test
    fun testScheduleUpcomingProcessorNonExistentLeague() {
        val incoming = givenIncomingMessageWithNonExistentLeague()
        val matches = givenMatchesFound()
        val sent = whenRequestingMatches(incoming)

        thenDataRequesterWasNotInvoked()
        thenMessageTemplateWasNotInvoked(matches)
        thenOutgoingTextIsCorrect(sent, "Unknown league alias: 'NonExistent'")
    }

    private fun givenMatchesFound(): UpcomingMatches =
            createMatches().apply {
                `when`(dataRequester.requestData(DEFAULT_LEAGUE)).thenReturn(this)
            }

    private fun givenValidIncomingMessage() =
            ProcessingInfo(1L, "$UPCOMING_COMMAND ${DEFAULT_LEAGUE.officialName}")

    private fun givenInvalidIncomingMessageFormat() =
            ProcessingInfo(1L, "Invalid")

    private fun givenIncomingMessageWithNonExistentLeague() =
            ProcessingInfo(1L, "$UPCOMING_COMMAND NonExistent")

    private fun givenMessageTemplateProcesses(): String =
            "Dummy".apply {
                `when`(messageTemplate.process(ArgumentMatchers.any(), ArgumentMatchers.any()))
                        .thenAnswer {
                            val writer = it.arguments[1] as Writer
                            writer.write(this)
                            null
                        }
            }


    private fun whenRequestingMatches(info: ProcessingInfo) =
            processor.processIncomingMessage(info) as SendMessage

    private fun thenDataRequesterWasInvoked() {
        verifyDataRequester()
    }

    private fun thenDataRequesterWasNotInvoked() {
        verifyDataRequester(never())
    }

    private fun thenMessageTemplateWasInvoked(matches: UpcomingMatches) {
        verifyMessageTemplate(matches)
    }

    private fun thenMessageTemplateWasNotInvoked(matches: UpcomingMatches) {
        verifyMessageTemplate(matches, never())
    }

    private fun thenOutgoingMessageIsComplete(message: SendMessage, text: String) {
        thenOutgoingTextIsCorrect(message, text)
        thenFooterIsCorrect(message)
    }

    private fun thenOutgoingTextIsCorrect(message: SendMessage, text: String) {
        assertEquals(text, message.text)
    }

    private fun thenFooterIsCorrect(message: SendMessage) {
        val firstButton = (message.replyMarkup as InlineKeyboardMarkup).keyboard.flatten().first()
        assertEquals("Back", firstButton.text)
        assertEquals(ProcessorType.START.path, firstButton.callbackData)
    }

    private fun verifyDataRequester(mode: VerificationMode? = times(1)) {
        verify(dataRequester, mode).requestData(DEFAULT_LEAGUE)
    }

    private fun verifyMessageTemplate(matches: UpcomingMatches, mode: VerificationMode? = times(1)) {
        verify(messageTemplate, mode).process(
                eq(mapOf(
                        "leagueName" to DEFAULT_LEAGUE,
                        "schedule" to matches.matches
                )),
                any(Writer::class.java)
        )
    }

    private fun createMatches(): UpcomingMatches = UpcomingMatches(mapOf(
            LocalDate.of(2019, 1, 1) to listOf(
                    MatchInfo("id1", "Game1", setOf("Team1", "Team2"), LocalDateTime.of(2019, 1, 1, 1, 1, 1)),
                    MatchInfo("id2", "Game2", setOf("Team3", "Team4"), LocalDateTime.of(2019, 1, 1, 2, 0, 0))
            ),
            LocalDate.of(2019, 1, 2) to listOf(
                    MatchInfo("id3", "Game1", setOf("Team2", "Team3"), LocalDateTime.of(2019, 1, 2, 1, 1, 1)),
                    MatchInfo("id4", "Game2", setOf("Team1", "Team4"), LocalDateTime.of(2019, 1, 2, 2, 0, 0))
            )
    ))

}