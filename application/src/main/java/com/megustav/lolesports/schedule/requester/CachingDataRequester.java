package com.megustav.lolesports.schedule.requester;

import com.megustav.lolesports.schedule.bot.LolEsportsScheduleBot;
import com.megustav.lolesports.schedule.cache.BotCacheManager;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A decorator requester caching data going through the main requester
 *
 * @author MeGustav
 *         20/04/2018 00:48
 */
public class CachingDataRequester<Req, Res> implements DataRequester<Req, Res> {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(LolEsportsScheduleBot.class);

    /** Main data requester */
    private final DataRequester<Req, Res> dataRequester;
    /** Cache */
    private final Cache<Req, Res> cache;

    public CachingDataRequester(String cacheName,
                                BotCacheManager cacheManager,
                                Class<Req> cacheKeyClass,
                                Class<Res> cacheValueClass,
                                DataRequester<Req, Res> dataRequester) {
        this.dataRequester = dataRequester;
        this.cache = cacheManager.createDefaultCache(
                cacheName,
                cacheKeyClass,
                cacheValueClass
        );
    }

    @Override
    public Res requestData(Req request) {
        log.debug("Getting cache for key: {}", request);
        if (cache.containsKey(request)) {
            log.debug("Got cache for key {}", request);
            return cache.get(request);
        }
        log.debug("Cache data expired for key {}. Refreshing", request);
        Res response = dataRequester.requestData(request);
        cache.put(request, response);
        return response;
    }
}
