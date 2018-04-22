package com.megustav.lolesports.schedule.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableMap;
import com.megustav.lolesports.schedule.configuration.FreemarkerConfiguration;
import com.megustav.lolesports.schedule.configuration.ProcessorConfiguration;
import com.megustav.lolesports.schedule.processor.upcoming.UpcomingMatchesProcessor;
import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import no.api.freemarker.java8.Java8ObjectWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Upcoming matches processor testing
 *
 * TODO introduction of cache made test codependent
 * TODO research Spring test independence
 *
 * @author MeGustav
 * 10/04/2018 00:25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessorConfiguration.class)
public class TestUpcomingMatchesProcessor {

    /** JSON mapper */
    private static final ObjectReader READER = new ObjectMapper()
            .readerFor(ScheduleInformation.class);

    /** Tested object */
    @Autowired
    private UpcomingMatchesProcessor processor;

    /**  */
    private final Configuration freemarker = createFreemarker();

    /** Mocked Riot API client */
    @MockBean
    private RiotApiClient client;

    /**
     * Test whether {@link ProcessorType#UPCOMING} produces correct {@link BotApiMethod}
     * when upcoming matches are present
     *
     * Provided interfaces leave no choice but to cast classes
     * in order to check the outcome
     */
    @Test
    public void testSimpleScheduleUpcomingProcessor() throws Exception {
        ScheduleInformation response = READER.readValue(
                TestUpcomingMatchesProcessor.class.getResource("/upcoming/base-riot-response.json"));
        Mockito.when(client.getSchedule(Mockito.any())).thenReturn(response);

        SendMessage sendMessage = requestUpcomingMatches(League.EULCS);
        String expectedPayload = evaluateTemplate("base-bot-response.md.ftl", ImmutableMap.of(
                "league", League.EULCS,
                "time", LocalTime.of(10, 10).toString()
        ));
        assertThat(sendMessage.getText()).as("Message text").isEqualTo(expectedPayload);
        // Checking the footer
        checkReplyMarkdown(sendMessage);
    }

    /**
     * Test whether {@link ProcessorType#UPCOMING} produces correct {@link BotApiMethod}
     * when there are no upcoming matches
     *
     * Provided interfaces ({@link BotApiMethod}) leave no choice but to cast classes
     * in order to check the outcome
     */
    @Test
    public void testEmptyScheduleUpcomingProcessor() throws Exception {
        ScheduleInformation response = READER.readValue(
                TestUpcomingMatchesProcessor.class.getResource("/upcoming/empty-schedule-riot-response.json"));
        Mockito.when(client.getSchedule(Mockito.any())).thenReturn(response);

        SendMessage sendMessage = requestUpcomingMatches(League.NALCS);
        String expectedPayload = evaluateTemplate("empty-schedule-bot-response.md.ftl", ImmutableMap.of(
                "league", League.NALCS
        ));
        assertThat(sendMessage.getText()).as("Message text").isEqualTo(expectedPayload);
        // Checking the footer
        checkReplyMarkdown(sendMessage);
    }

    /**
     * Test whether {@link ProcessorType#UPCOMING} produces correct {@link BotApiMethod}
     * Check that caching is done
     *
     * Provided interfaces ({@link BotApiMethod}) leave no choice but to cast classes
     * in order to check the outcome
     */
    @Test
    public void testScheduleUpcomingProcessorCache() throws Exception {
        // Prepare first riot API response
        ScheduleInformation initialResponse = READER.readValue(
                TestUpcomingMatchesProcessor.class.getResource("/upcoming/base-riot-response.json"));
        Mockito.when(client.getSchedule(Mockito.any())).thenReturn(initialResponse);

        SendMessage sendMessage = requestUpcomingMatches(League.LCK);
        String expectedPayload = evaluateTemplate("base-bot-response.md.ftl", ImmutableMap.of(
                "league", League.LCK,
                "time", LocalTime.of(10, 10).toString()
        ));
        assertThat(sendMessage.getText()).as("Message text").isEqualTo(expectedPayload);

        // Prepare new riot API response
        ScheduleInformation newResponse = READER.readValue(
                TestUpcomingMatchesProcessor.class.getResource("/upcoming/cache-test-riot-response.json"));
        Mockito.when(client.getSchedule(Mockito.any())).thenReturn(newResponse);

        // Asserting that LCK was cached and will not return new matches
        sendMessage = requestUpcomingMatches(League.LCK);
        assertThat(sendMessage.getText()).as("Message text").isEqualTo(expectedPayload);

        // Checking the footer
        checkReplyMarkdown(sendMessage);
    }

    /**
     * Request bot for upcoming matches
     *
     * @return response message
     */
    private SendMessage requestUpcomingMatches(League league) throws Exception {
        BotApiMethod<Message> preparedMethod = processor.processIncomingMessage(
                new ProcessingInfo(1L, "/upcoming " + league.getOfficialName()));
        assertThat(SendMessage.class.isInstance(preparedMethod))
                .as("UPCOMING processor produces a SendMessage instance")
                .isTrue();

        return SendMessage.class.cast(preparedMethod);
    }

    /**
     * Checks upcoming schedule reply markdown
     *
     * Provided interfaces ({@link ReplyKeyboard}) leave no choice but to cast classes
     * in order to check the outcome
     *
     * @param sendMessage message to be sent
     */
    private void checkReplyMarkdown(SendMessage sendMessage) {
        ReplyKeyboard replyMarkup = sendMessage.getReplyMarkup();
        assertThat(InlineKeyboardMarkup.class.isInstance(replyMarkup))
                .as("Reply markup is of inline keyboard type")
                .isTrue();
        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.class.cast(replyMarkup);
        List<InlineKeyboardButton> allButtons = keyboardMarkup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(allButtons.size())
                .as("Overall buttons on UPCOMING")
                .isEqualTo(1);
        assertThat(allButtons.get(0).getText())
                .as("Upcoming matches response button text")
                .isEqualTo("Back");
    }

    /**
     * Evaluate Freemarker template
     *
     * @param fileName template file name
     * @param params template params
     * @return evaluated template
     */
    private String evaluateTemplate(String fileName, Map<String, Object> params) throws IOException, TemplateException {
        Template template = freemarker.getTemplate(fileName);
        try (StringWriter out = new StringWriter()) {
            template.process(params, out);
            return out.toString();
        }
    }

    /**
     * @return freemarker configuration
     */
    private Configuration createFreemarker() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassForTemplateLoading(FreemarkerConfiguration.class, "/upcoming/template/");
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setObjectWrapper(new Java8ObjectWrapper(Configuration.getVersion()));
        return configuration;
    }

}
