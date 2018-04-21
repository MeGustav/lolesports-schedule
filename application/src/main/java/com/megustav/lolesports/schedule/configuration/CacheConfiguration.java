package com.megustav.lolesports.schedule.configuration;

import com.megustav.lolesports.schedule.cache.BotCacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration
 *
 * @author MeGustav
 *         19/04/2018 22:10
 */
@Configuration
public class CacheConfiguration {

    /**
     * @return cache manager
     */
    @Bean
    public BotCacheManager cacheManager() {
        return new BotCacheManager(
                CacheManagerBuilder.newCacheManagerBuilder().build(true)
        );
    }
}
