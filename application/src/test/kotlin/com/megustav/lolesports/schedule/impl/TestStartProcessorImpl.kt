package com.megustav.lolesports.schedule.impl

import com.megustav.lolesports.schedule.processor.ProcessingInfo
import com.megustav.lolesports.schedule.processor.ProcessorRepository
import com.megustav.lolesports.schedule.processor.impl.StartProcessor
import com.megustav.lolesports.schedule.riot.League
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author MeGustav
 *         2019-02-14 20:41
 */
@RunWith(MockitoJUnitRunner::class)
class TestStartProcessorImpl {

    private val processor = StartProcessor(
            Mockito.mock(ProcessorRepository::class.java)
    )

    /**
     * Test whether [ProcessorType.START] produces correct [BotApiMethod]
     *
     * Provided interfaces leave no choice but to cast classes
     * in order to check the outcome
     */
    @Test
    fun testStartProcessor() {
        val preparedMethod = processor.processIncomingMessage(ProcessingInfo(1L))

        assertTrue { preparedMethod is SendMessage }
        val sendMessage = preparedMethod as SendMessage
        assertEquals("Upcoming matches for:", sendMessage.text)

        val replyMarkup = sendMessage.replyMarkup
        assertTrue { replyMarkup is InlineKeyboardMarkup }
        val keyboardMarkup = replyMarkup as InlineKeyboardMarkup
        val allButtons = keyboardMarkup.keyboard.flatten().toSet()
        assertEquals(League.values().size, allButtons.size)
    }

}