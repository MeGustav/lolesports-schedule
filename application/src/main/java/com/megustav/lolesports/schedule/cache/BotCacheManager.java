package com.megustav.lolesports.schedule.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import java.time.Duration;

/**
 * Bot cache manager
 * 
 * @author MeGustav
 *         20/04/2018 00:16
 */
public class BotCacheManager {

    /** Default amount of heap space could be occupied */
    private static final long CACHE_DEFAULT_HEAP_LIMIT = 200;
    /** Default cache expiry */
    private static final Duration CACHE_DEFAULT_EXPIRY = Duration.ofHours(1);
    
    /** Ehcache cache manager */
    private final CacheManager cacheManager;

    public BotCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     *
     * @param alias cache alias
     * @param keyClass keyClass class
     * @param valueClass valueClass class
     * @param <K> type of the key class
     * @param <V> type of the value class
     * @return cache
     */
    public <K, V> Cache<K, V> createDefaultCache(String alias, Class<K> keyClass, Class<V> valueClass) {
        return createCache(alias, keyClass, valueClass, CACHE_DEFAULT_HEAP_LIMIT, CACHE_DEFAULT_EXPIRY);
    }

    /**
     * Create cache
     *
     * @param alias cache alias
     * @param keyClass key class
     * @param valueClass value class
     * @param cacheSize cache heap size limit
     * @param expiry cache expiry
     * @param <K> type of the key class
     * @param <V> type of the value class
     * @return cache
     */
    public <K, V> Cache<K, V> createCache(String alias, Class<K> keyClass, Class<V> valueClass, long cacheSize, Duration expiry) {
        return cacheManager.createCache(alias,
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(keyClass, valueClass,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().heap(cacheSize, MemoryUnit.MB))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(expiry)));
    }
}
