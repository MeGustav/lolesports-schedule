package com.megustav.lolesports.schedule.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.megustav.lolesports.schedule.configuration.ProcessorConfiguration;
import com.megustav.lolesports.schedule.riot.League;
import com.megustav.lolesports.schedule.riot.RiotApiClient;
import com.megustav.lolesports.schedule.riot.mapping.ScheduleInformation;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Upcoming matches processor tesing
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

    /** Processor repository */
    @Autowired
    private ProcessorRepository repository;

    /** Mocked Riot API client */
    @MockBean
    private RiotApiClient client;

    /**
     * Setting up {@link RiotApiClient} bean
     */
    @Before
    public void before() throws IOException {
        ScheduleInformation response = READER.readValue(
                TestUpcomingMatchesProcessor.class.getResource("/upcoming/base-riot-response.json"));
        Mockito.when(client.getSchedule(Mockito.any())).thenReturn(response);
    }

    /**
     * Test whether {@link ProcessorType#START} produces correct {@link BotApiMethod}
     *
     * Provided interfaces leave no choice but to cast classes
     * in order to check the outcome
     */
    @Test
    public void testUpcomingProcessor() throws Exception {
        MessageProcessor processor = repository.getProcessor(ProcessorType.UPCOMING)
                .orElseThrow(() -> new IllegalStateException("UPCOMING processor not found"));
        BotApiMethod<Message> preparedMethod = processor.processIncomingMessage(
                new ProcessingInfo(1L, "/upcoming " + League.EULCS.getOfficialName()));
        assertThat(SendMessage.class.isInstance(preparedMethod))
                .as("UPCOMING processor produces a SendMessage instance")
                .isTrue();

        SendMessage sendMessage = SendMessage.class.cast(preparedMethod);
        String expectedPayload = IOUtils.toString(TestUpcomingMatchesProcessor.class
                .getResource("/upcoming/base-bot-response.markdown"), StandardCharsets.UTF_8)
                // Swapping placeholders with correctly zoned time
                .replace("$TIME$", LocalTime.of(10, 10).toString());
        assertThat(sendMessage.getText()).as("Message text").isEqualTo(expectedPayload);

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

}
