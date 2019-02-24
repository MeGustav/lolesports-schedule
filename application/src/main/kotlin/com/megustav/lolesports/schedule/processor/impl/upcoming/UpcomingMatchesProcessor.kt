package com.megustav.lolesports.schedule.processor.impl.upcoming

import com.megustav.lolesports.schedule.bot.MessageUtils.Companion.consumeMessage
import com.megustav.lolesports.schedule.bot.MessageUtils.Companion.createMessage
import com.megustav.lolesports.schedule.processor.*
import com.megustav.lolesports.schedule.requester.DataRequester
import com.megustav.lolesports.schedule.riot.League
import com.megustav.lolesports.schedule.riot.MatchInfo
import freemarker.template.Configuration
import freemarker.template.Template
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.io.StringWriter
import java.time.LocalDate
import java.util.regex.Pattern

/**
 *
 * @author MeGustav
 *         2019-02-23 15:01
 */
class UpcomingMatchesProcessor(private val dataRequester: DataRequester<League, UpcomingMatches>,
                               private val repository: ProcessorRepository,
                               private val messageTemplate: Template): MessageProcessor {

    constructor(dataRequester: DataRequester<League, UpcomingMatches>,
                repository: ProcessorRepository,
                configuration: Configuration) :
            this(dataRequester, repository, configuration.getTemplate(TEMPLATE_NAME))

    companion object {
        private const val TEMPLATE_NAME = "upcoming.ftl"
        private val MESSAGE_PATTERN =
                Pattern.compile(ProcessorType.UPCOMING.path + "\\s(?<league>\\w+).*")
        private val log = LoggerFactory.getLogger(UpcomingMatchesProcessor::class.java)
    }

    fun init() = repository.register(this)

    override fun processIncomingMessage(processingInfo: ProcessingInfo): BotApiMethod<Message> {
        val chatId = processingInfo.chatId

        consumeMessage(log::debug, chatId, "Processing a full schedule request...")
        consumeMessage(log::debug, chatId, "Received data: $processingInfo")

        val payload: String = processingInfo.payload
                ?: "Empty message".let {
                    consumeMessage(log::debug, chatId, it)
                    return createMessage(chatId, it)
                }

        val matcher = MESSAGE_PATTERN.matcher(payload)
        if (!matcher.matches()) {
            val message = "Unexpected message: '$payload'"
            consumeMessage(log::debug, chatId, message)
            return createMessage(chatId, message)
        }

        val requestedLeague = matcher.group("league")
        val league = League.fromAlias(requestedLeague)
                ?: "Unknown league alias: '$requestedLeague'".let {
                    consumeMessage(log::debug, chatId, it)
                    return createMessage(chatId, it)
                }

        val matches = dataRequester.requestData(league).matches
        val responsePayload = formMessagePayload(league, matches)
        return createMessage(chatId, responsePayload).also {
            appendFooter(it)
            consumeMessage(log::debug, chatId, "Prepared response: $it")
        }
    }

    override val type: ProcessorType
        get() = ProcessorType.UPCOMING

    private fun formMessagePayload(league: League,
                                   matches: Map<LocalDate, List<MatchInfo>>): String {
        val params = mapOf("leagueName" to league, "schedule" to matches)
        StringWriter().use { out ->
            messageTemplate.process(params, out)
            return out.toString()
        }
    }

    private fun appendFooter(message: SendMessage) {
        message.replyMarkup = InlineKeyboardMarkup().setKeyboard(
                listOf(listOf(
                        InlineKeyboardButton("Back")
                                .setCallbackData(ProcessorType.START.path)
                ))
        )
    }

}