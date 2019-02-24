package com.megustav.lolesports.schedule.configuration

import com.megustav.lolesports.schedule.processor.ProcessorRepository
import com.megustav.lolesports.schedule.processor.UpcomingMatches
import com.megustav.lolesports.schedule.processor.impl.StartProcessor
import com.megustav.lolesports.schedule.processor.impl.upcoming.UpcomingMatchesProcessor
import com.megustav.lolesports.schedule.processor.impl.upcoming.UpcomingMatchesTransformer
import com.megustav.lolesports.schedule.requester.CachingDataRequester
import com.megustav.lolesports.schedule.requester.DataRequester
import com.megustav.lolesports.schedule.requester.UpcomingMatchesRequester
import com.megustav.lolesports.schedule.riot.League
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author MeGustav
 *         2019-02-24 13:19
 */
@Configuration
@Import(ScheduleApiConfiguration::class,
        CacheConfiguration::class,
        FreemarkerConfiguration::class)
open class ProcessorConfiguration @Autowired constructor(
        private val apiConfiguration: ScheduleApiConfiguration,
        private val cacheConfiguration: CacheConfiguration,
        private val freemarkerConfiguration: FreemarkerConfiguration
) {

    @Bean
    open fun processorRepository(): ProcessorRepository = ProcessorRepository()

    @Bean(initMethod = "init")
    open fun upcomingMatchesProcessor(): UpcomingMatchesProcessor = UpcomingMatchesProcessor(
            cachingUpcomingRequester(),
            processorRepository(),
            freemarkerConfiguration.upcomingTemplateEvaluator()
    )

    @Bean(initMethod = "init")
    open fun startProcessor(): StartProcessor = StartProcessor(processorRepository())

    @Bean
    open fun upcomingMatchesTransformer(): UpcomingMatchesTransformer = UpcomingMatchesTransformer()

    @Bean
    open fun cachingUpcomingRequester(): DataRequester<League, UpcomingMatches> = CachingDataRequester(
            "UPCOMING",
            cacheConfiguration.cacheManager(),
            League::class.java,
            UpcomingMatches::class.java,
            // For now using the requester
            // which gets data using Riot API.
            // Later this could be swapped with, say,
            // database data requester
            upcomingMatchesRequester()
    )

    @Bean
    open fun upcomingMatchesRequester(): DataRequester<League, UpcomingMatches> = UpcomingMatchesRequester(
            apiConfiguration.scheduleApiClient(), upcomingMatchesTransformer()
    )
}