package com.megustav.lolesports.schedule.requester

import com.megustav.lolesports.schedule.cache.BotCacheManager
import org.ehcache.Cache
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A decorator requester caching data going through the main requester
 *
 * TODO probably could be not that explicit as there are tons of caching utilities
 */
class CachingDataRequester<Req, Res>: DataRequester<Req, Res> {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UpcomingMatchesRequester::class.java)
    }

    /** Main data requester */
    private val dataRequester: DataRequester<Req, Res>
    /** Cache */
    private val cache: Cache<Req, Res>

    constructor(cacheName: String,
                cacheManager: BotCacheManager,
                cacheKeyClass: Class<Req>,
                cacheValueClass: Class<Res>,
                dataRequester: DataRequester<Req, Res>) {
        this.dataRequester = dataRequester
        this.cache = cacheManager.createDefaultCache(
                cacheName,
                cacheKeyClass,
                cacheValueClass
        )
    }

    override fun requestData(request: Req): Res {
        log.debug("Getting cache for key: $request")

        return if (cache.containsKey(request)) {
            log.debug("Got cache for key $request")
            cache.get(request)
        } else {
            log.debug("Cache data expired for key $request. Refreshing")
            dataRequester.requestData(request).also {
                cache.put(request, it)
            }
        }

    }

}