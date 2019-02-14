package com.megustav.lolesports.schedule.context

import com.megustav.lolesports.schedule.configuration.ProcessorConfiguration
import com.megustav.lolesports.schedule.processor.ProcessorRepository
import com.megustav.lolesports.schedule.processor.ProcessorType
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertTrue

/**
 * @author MeGustav
 *         2019-02-14 20:12
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [ProcessorConfiguration::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestProcessorRepository {

    @Autowired
    private lateinit var repository: ProcessorRepository

    /**
     * Test whether all processors were registered
     */
    @Test
    fun testAllProcessorsRegistered() {

        ProcessorType.values().forEach {
            assertTrue { repository.getProcessor(it).isPresent }
        }
    }

}