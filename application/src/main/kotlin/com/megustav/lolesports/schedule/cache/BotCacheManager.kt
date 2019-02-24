package com.megustav.lolesports.schedule.cache

import org.ehcache.Cache
import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.MemoryUnit

import java.time.Duration

/**
 * @author MeGustav
 *         20/04/2018 00:16
 */
class BotCacheManager(private val cacheManager: CacheManager) {

    companion object {
        /** Default amount of heap space could be occupied  */
        private const val CACHE_DEFAULT_HEAP_LIMIT: Long = 200
        /** Default cache expiry  */
        private val CACHE_DEFAULT_EXPIRY = Duration.ofHours(1)
    }

    fun <K, V> createDefaultCache(alias: String, keyClass: Class<K>, valueClass: Class<V>): Cache<K, V> =
            createCache(alias, keyClass, valueClass, CACHE_DEFAULT_HEAP_LIMIT, CACHE_DEFAULT_EXPIRY)

    private fun <K, V> createCache(
            alias: String,
            keyClass: Class<K>,
            valueClass: Class<V>,
            cacheSize: Long, expiry: Duration
    ): Cache<K, V> =
            CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(
                            keyClass,
                            valueClass,
                            ResourcePoolsBuilder.newResourcePoolsBuilder()
                                    .heap(cacheSize, MemoryUnit.MB)
                    )
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(expiry))
                    .let {
                        cacheManager.createCache(alias, it)
                    }
}
