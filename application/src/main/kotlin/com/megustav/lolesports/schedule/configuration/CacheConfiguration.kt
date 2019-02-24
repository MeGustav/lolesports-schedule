package com.megustav.lolesports.schedule.configuration

import com.megustav.lolesports.schedule.cache.BotCacheManager
import org.ehcache.config.builders.CacheManagerBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author MeGustav
 *         19/04/2018 22:10
 */
@Configuration
open class CacheConfiguration {

    @Bean
    open fun cacheManager(): BotCacheManager =
            BotCacheManager(
                    CacheManagerBuilder.newCacheManagerBuilder().build(true)
            )
}
