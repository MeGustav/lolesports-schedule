package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.data.MatchInfo;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import com.megustav.lolesports.schedule.riot.transformer.UpcomingMatchesTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.megustav.lolesports.schedule.bot.MessageUtils.consumeMessage;

/**
 * Class for full schedule requests processing
 *
 * @author MeGustav
 *         15/02/2018 22:09
 */
public class UpcomingMatchesProcessor implements MessageProcessor {

    /** Named events */
    private final Set<String> MAIN_EVENTS =
            Stream.of("quarterfinal", "semifinal", "grand-final", "third-place")
                    .collect(Collectors.toSet());

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

    public UpcomingMatchesProcessor(RiotApiClient apiClient,
                                    ProcessorRepository repository,
                                    UpcomingMatchesTransformer transformer) {
        this.apiClient = apiClient;
        this.repository = repository;
        this.transformer = transformer;
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
    public BotApiMethod<Message> processIncomingMessage(ProcessingInfo processingInfo) {
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
    private String formMessagePayload(League league, Map<LocalDate, List<MatchInfo>> matches) {
        StringBuilder sb = new StringBuilder()
                .append(wrapped(league.getOfficialName().toUpperCase(), '*'))
                .append("\n\n");
        for (Map.Entry<LocalDate, List<MatchInfo>> entry : matches.entrySet()) {
            sb.append(wrapped(entry.getKey().format(DateTimeFormatter.ISO_DATE), '*'))
                    .append("\n");
            for (MatchInfo info : entry.getValue()) {
                sb.append(wrapped(info.getTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME), '`'))
                        .append("\t");
                String matchName = info.getName();
                if (matchName != null && isMainEvent(matchName)) {
                    sb.append(matchName.toUpperCase())
                            .append("\t");
                }
                sb.append(info.getTeams().stream().collect(Collectors.joining(" vs ")))
                        .append("\n");
            }
        }
        return sb.toString();
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

    /**
     * Determine whether or not event is main
     *
     * @param name event name
     * @return whether or not event is main
     */
    private boolean isMainEvent(String name) {
        return MAIN_EVENTS.stream()
                .anyMatch(event -> name.contains(event));
    }

    /**
     * Forms a wrapped in a symbol text
     *
     * @param text text
     * @param symbol symbol to wrap the text
     * @return wrapped text
     */
    private String wrapped(String text, char symbol) {
        return symbol + text + symbol;
    }

}
