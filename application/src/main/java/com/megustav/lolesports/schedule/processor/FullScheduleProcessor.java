package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.data.MatchInfo;
import com.megustav.lolesports.schedule.riot.mapping.Bracket;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleItem;
import com.megustav.lolesports.schedule.riot.mapping.TournamentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.megustav.lolesports.schedule.bot.MessageUtils.consumeMessage;

/**
 * Class for full schedule requests processing
 *
 * @author MeGustav
 *         15/02/2018 22:09
 */
public class FullScheduleProcessor implements MessageProcessor {

    /**  */
    private static final Pattern MESSAGE_PATTERN =
            Pattern.compile(ProcessorType.FULL_SCHEDULE.getPath() + "\\s(?<league>\\w+).*");

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);
    /** Riot api */
    private final RiotApiClient apiClient;
    /** Processor repository */
    private final ProcessorRepository repository;

    public FullScheduleProcessor(RiotApiClient apiClient, ProcessorRepository repository) {
        this.apiClient = apiClient;
        this.repository = repository;
    }

    /**
     * Initialization
     */
    public void init() {
        repository.register(this);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.FULL_SCHEDULE;
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


        List<MatchInfo> matchList = getMatchesToSend(schedule);
        Map<LocalDate, List<MatchInfo>> result = matchList.stream()
                .collect(Collectors.groupingBy(info -> info.getTime().toLocalDate()));

        StringBuilder sb = new StringBuilder()
                .append(wrapped(leagueOpt.get().getOfficialName().toUpperCase(), '*'))
                .append("\n\n");
        for (Map.Entry<LocalDate, List<MatchInfo>> entry : result.entrySet()) {
            sb.append(wrapped(entry.getKey().format(DateTimeFormatter.ISO_DATE), '*'))
                    .append("\n");
            for (MatchInfo info : entry.getValue()) {
                sb.append(wrapped(info.getTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME), '`'))
                        .append("\t")
                        .append(info.getName())
                        .append("\n");
            }
        }

        SendMessage response = new SendMessage(chatId, sb.toString()).enableMarkdown(true);
        appendFooter(response);

        consumeMessage(log::debug, chatId, "Prepared response: " + response);
        consumeMessage(log::trace, chatId,
                "Message processed in " + (System.currentTimeMillis() - start) + "ms");
        return response;
    }

    /**
     * Filtering the schedule and forming outgoing message
     * This as well is to be annihilated when persistence is implemented
     * TODO persistent data
     *
     * @param schedule schedule information
     * @return list of matches to be sent
     */
    private List<MatchInfo> getMatchesToSend(ScheduleInformation schedule) {
        List<MatchInfo> matchList = new ArrayList<>();
        Comparator<ScheduleItem> comparator = Comparator.comparing(ScheduleItem::getTime);
        schedule.getScheduleItems().stream()
                .filter(item -> item.getTime().after(new Date()))
                .sorted(comparator)
                .limit(10)
                .forEach(item -> schedule.getTournaments().stream()
                        .filter(tournament -> Objects.equals(tournament.getId(), item.getTournament()))
                        .map(TournamentInfo::getBrackets)
                        .filter(brackets -> brackets.containsKey(item.getBracket()))
                        .map(brackets -> brackets.get(item.getBracket()))
                        .map(Bracket::getMatches)
                        .filter(matches -> matches.containsKey(item.getMatch()))
                        .map(matches -> matches.get(item.getMatch()))
                        .forEach(match -> matchList.add(new MatchInfo(
                                match.getId(),
                                match.getName(),
                                LocalDateTime.ofInstant(item.getTime().toInstant(), ZoneId.systemDefault())))));
        return matchList;
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
