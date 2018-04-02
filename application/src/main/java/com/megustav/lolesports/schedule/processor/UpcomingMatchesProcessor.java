package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.data.MatchInfo;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import com.megustav.lolesports.schedule.riot.transformer.UpcomingMatchesTransformer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.megustav.lolesports.schedule.bot.MessageUtils.consumeMessage;

/**
 * Class for full schedule requests processing
 *
 * @author MeGustav
 *         15/02/2018 22:09
 */
public class UpcomingMatchesProcessor implements MessageProcessor {

    /** Message pattern */
    private static final Pattern MESSAGE_PATTERN =
            Pattern.compile(ProcessorType.UPCOMING.getPath() + "\\s(?<league>\\w+).*");

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);
    /** Riot api */
    private final RiotApiClient apiClient;
    /** Processor repository */
    private final ProcessorRepository repository;
    /** Upcoming matches transformer */
    private final UpcomingMatchesTransformer transformer;
    /** Parsed template for forming messages */
    private final Template messageTemplate;

    public UpcomingMatchesProcessor(RiotApiClient apiClient,
                                    ProcessorRepository repository,
                                    UpcomingMatchesTransformer transformer,
                                    Configuration configuration) throws IOException {
        this.apiClient = apiClient;
        this.repository = repository;
        this.transformer = transformer;
        this.messageTemplate = configuration.getTemplate("upcoming.ftl");
    }

    /**
     * Initialization
     */
    public void init() {
        repository.register(this);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.UPCOMING;
    }

    /**
     * Process incoming message
     *
     * @param processingInfo incoming message
     */
    @Override
    public BotApiMethod<Message> processIncomingMessage(ProcessingInfo processingInfo)
            throws IOException, TemplateException {
        long start = System.currentTimeMillis();
        long chatId = processingInfo.getChatId();

        consumeMessage(log::debug, chatId, "Processing a full schedule request...");
        consumeMessage(log::trace, chatId, "Received data: " + processingInfo);

        String payload = processingInfo.getPayload();
        Matcher matcher = MESSAGE_PATTERN.matcher(payload);
        if (! matcher.matches()) {
            String message = "Unexpected message: '" + payload + "'";
            consumeMessage(log::debug, chatId, message);
            return new SendMessage(chatId, message);
        }

        String requestedLeague = matcher.group("league");
        Optional<League> leagueOpt = League.fromAlias(requestedLeague);
        if (! leagueOpt.isPresent()) {
            String message = "Unknown league alias: '" + requestedLeague + "'";
            consumeMessage(log::debug, chatId, message);
            return new SendMessage(chatId, message);
        }

        // For now just getting the schedule by http request.
        // Obviously this kind of info is to be persisted
        ScheduleInformation schedule = apiClient.getSchedule(leagueOpt.get());
        // Transforming data into convenient form
        Map<LocalDate, List<MatchInfo>> matches = transformer.transform(schedule);
        // Forming message payload
        String responsePayload = formMessagePayload(leagueOpt.get(), matches);

        SendMessage response = new SendMessage(chatId, responsePayload).enableMarkdown(true);
        appendFooter(response);

        consumeMessage(log::debug, chatId, "Prepared response: " + response);
        consumeMessage(log::trace, chatId,
                "Message processed in " + (System.currentTimeMillis() - start) + "ms");
        return response;
    }

    /**
     * Form message payload
     *
     * @param league league
     * @param matches matches info
     * @return message payload
     */
    private String formMessagePayload(League league, Map<LocalDate, List<MatchInfo>> matches)
            throws IOException, TemplateException {
        Map<String, Object> params = new HashMap<>();
        params.put("leagueName", league);
        params.put("schedule", matches);
        try (StringWriter out = new StringWriter()) {
            messageTemplate.process(params, out);
            return out.toString();
        }
    }

    /**
     * Appends the footer back button
     *
     * @param message message
     */
    private void appendFooter(SendMessage message) {
        message.setReplyMarkup(new InlineKeyboardMarkup()
                .setKeyboard(Collections.singletonList(Collections.singletonList(
                        new InlineKeyboardButton("Back").setCallbackData(ProcessorType.START.getPath())
                ))));
    }

}
