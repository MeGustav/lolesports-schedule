package com.megustav.lolesports.schedule.processor;

import com.megustav.lolesports.schedule.configuration.ProcessorConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Processors testing
 *
 * @author MeGustav
 *         06/04/2018 17:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessorConfiguration.class)
public class TestProcessorRepository {

    /** Processor repository */
    @Autowired
    private ProcessorRepository repository;

    /**
     * Test whether all processors were registered
     */
    @Test
    public void testAllProcessorsRegistered() {
        Stream.of(ProcessorType.values())
                .forEach(type -> {
                    Optional<MessageProcessor> processor = repository.getProcessor(type);
                    assertThat(processor.isPresent())
                            .as("Processor of type '" + type + "' registered")
                            .isTrue();
                });
    }
}
