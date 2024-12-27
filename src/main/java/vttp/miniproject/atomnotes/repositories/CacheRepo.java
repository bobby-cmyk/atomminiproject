package vttp.miniproject.atomnotes.repositories;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import vttp.miniproject.atomnotes.models.UserStats;

@Repository
public class CacheRepo {

    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;
    
    private final String QUOTE_PREFIX = "quote";
    private final String STATS_PREFIX = "stats:";

    // exists quote
    public boolean checkQuoteCached() {

        return template.hasKey(QUOTE_PREFIX);
        
    }

    // set quote quote ex 600
    public void cacheQuote(String quote) {

        ValueOperations<String, String> valueOps = template.opsForValue();

        valueOps.set(QUOTE_PREFIX, quote, Duration.ofMinutes(60));
    }

    // get quote
    public String retrieveCachedQuote() {

        ValueOperations<String, String> valueOps = template.opsForValue();

        return valueOps.get(QUOTE_PREFIX);
    }

    // exists "stats:userId"
    public boolean checkStatsCached(String userId) {

        return template.hasKey(STATS_PREFIX + userId);
    }

    // set "stats:userId" values ex 600
    public void cacheStats(UserStats userStats, String userId) {

        HashOperations<String, String, String> hashOps = template.opsForHash();
        
        Map<String, String> values = userStats.userStatsToMap();

        hashOps.putAll(STATS_PREFIX + userId, values);

        // Set expiration, in the case that condition (Tasks change but Number of task and completed tasks remain the same for too long)
        template.expire(STATS_PREFIX + userId, Duration.ofMinutes(60));
    }

    // hgetall "stats:userId"
    public UserStats retrieveCacheStats(String userId) {

        HashOperations<String, String, String> hashOps = template.opsForHash();
        
        Map<String, String> statsMap = hashOps.entries(STATS_PREFIX + userId);

        UserStats userStats = UserStats.mapToUserStats(statsMap);

        return userStats;
    }


}
