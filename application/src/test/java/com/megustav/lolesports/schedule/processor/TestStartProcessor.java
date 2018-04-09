package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.configuration.ProcessorConfiguration;
import com.megustav.lolesports.schedule.riot.League;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Start processor testing
 *
 * @author MeGustav
 *         10/04/2018 00:25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessorConfiguration.class)
public class TestStartProcessor {

    /** Processor repository */
    @Autowired
    private ProcessorRepository repository;

    /**
     * Test whether {@link ProcessorType#START} produces correct {@link BotApiMethod}
     *
     * Provided interfaces leave no choice but to cast classes
     * in order to check the outcome
     */
    @Test
    public void testStartProcessor() throws Exception {
        BotApiMethod<Message> preparedMethod = repository.getProcessor(ProcessorType.START)
                .orElseThrow(() -> new IllegalStateException("START processor not found"))
                .processIncomingMessage(new ProcessingInfo(1L, null));
        assertThat(SendMessage.class.isInstance(preparedMethod))
                .as("START processor produces a SendMessage instance")
                .isTrue();
        SendMessage sendMessage = SendMessage.class.cast(preparedMethod);
        assertThat(sendMessage.getText())
                .as("Message text")
                .isEqualTo("Upcoming matches for:");
        ReplyKeyboard replyMarkup = sendMessage.getReplyMarkup();
        assertThat(InlineKeyboardMarkup.class.isInstance(replyMarkup))
                .as("Reply markup is of inline keyboard type")
                .isTrue();
        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.class.cast(replyMarkup);
        Set<InlineKeyboardButton> allButtons = keyboardMarkup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        assertThat(allButtons.size())
                .as("Overall buttons on START")
                .isEqualTo(League.values().length);
    }

}
