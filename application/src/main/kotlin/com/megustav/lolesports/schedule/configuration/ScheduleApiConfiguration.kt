package com.megustav.lolesports.schedule.configuration

import com.megustav.lolesports.schedule.riot.ScheduleApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author MeGustav
 *         25/01/2018 22:25
 */
@Configuration
open class ScheduleApiConfiguration {

    @Bean
    open fun scheduleApiClient(): ScheduleApiClient = ScheduleApiClient()

}
