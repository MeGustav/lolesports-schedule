package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.data.MatchInfo;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.mapping.Bracket;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleItem;
import com.megustav.lolesports.schedule.riot.mapping.TournamentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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
     * @param incomingMessage incoming message
     */
    @Override
    public BotApiMethod<Message> processIncomingMessage(Message incomingMessage) {
        long start = System.currentTimeMillis();
        Long chatId = incomingMessage.getChatId();

        consumeMessage(log::debug, chatId, "Processing a full schedule request...");
        consumeMessage(log::trace, chatId, "Received data: " + incomingMessage);

        String messageText = incomingMessage.getText();
        Matcher matcher = MESSAGE_PATTERN.matcher(messageText);
        if (! matcher.matches()) {
            String message = "Unexpected message: '" + messageText + "'";
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

        // Filtering the schedule and forming outgoing message
        // This as well is to be annihilated when persistence is implemented
        // TODO persistent data
        List<MatchInfo> result = new ArrayList<>();
        Comparator<ScheduleItem> comparator = Comparator.comparing(ScheduleItem::getTime);
        schedule.getScheduleItems().stream()
                .filter(item -> item.getTime().after(new Date()))
                .sorted(comparator)
                .limit(10)
                .forEach(item -> {
                    schedule.getTournaments().stream()
                            .filter(tournament -> Objects.equals(tournament.getId(), item.getTournament()))
                            .map(TournamentInfo::getBrackets)
                            .filter(brackets -> brackets.containsKey(item.getBracket()))
                            .map(brackets -> brackets.get(item.getBracket()))
                            .map(Bracket::getMatches)
                            .filter(matches -> matches.containsKey(item.getMatch()))
                            .map(matches -> matches.get(item.getMatch()))
                            .forEach(match -> result.add(new MatchInfo(match.getId(), match.getName(), item.getTime())));

                });

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm Z");
        String content = result.stream()
                .map(match -> MessageFormat.format("|{0}| {1}", match.getName(), sdf.format(match.getTime())))
                .collect(Collectors.joining("\n\n"));

        SendMessage response = new SendMessage(chatId, content);
        consumeMessage(log::debug, chatId, "Prepared response: " + response);
        consumeMessage(log::trace, chatId,
                "Message processed in " + (System.currentTimeMillis() - start) + "ms");
        return response;
    }


}
