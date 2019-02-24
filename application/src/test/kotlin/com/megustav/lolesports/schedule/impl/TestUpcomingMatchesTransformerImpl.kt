package com.megustav.lolesports.schedule.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.megustav.lolesports.schedule.processor.impl.upcoming.UpcomingMatchesTransformer
import com.megustav.lolesports.schedule.riot.json.ScheduleInformation
import org.junit.Test
import kotlin.test.assertEquals

/**
 * @author MeGustav
 *         2019-02-14 22:52
 */
class TestUpcomingMatchesTransformerImpl {

    companion object {
        private val READER = ObjectMapper()
                .readerFor(ScheduleInformation::class.java)
    }

    private val transformer = UpcomingMatchesTransformer()

    @Test
    fun testBasicTransformation() {
        val schedule = READER.readValue<ScheduleInformation>(
                UpcomingMatchesTransformer::class.java.getResource("/processor/base-riot-response.json"))
        val matches = transformer.transform(schedule)
        assertEquals(2, matches.size)
    }
}