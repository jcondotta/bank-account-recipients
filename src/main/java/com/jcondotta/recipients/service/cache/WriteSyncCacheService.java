package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.service.dto.RecipientsDTO;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Singleton
public class WriteSyncCacheService {

    private final RedisCommands<String, RecipientsDTO> redisCommands;
    private final Long timeToLiveInSeconds;

    @Inject
    public WriteSyncCacheService(RedisCommands<String, RecipientsDTO> redisCommands, @Value("${redis.cache.recipients.ttl-in-seconds}") Long timeToLiveInSeconds) {
        this.redisCommands = redisCommands;
        this.timeToLiveInSeconds = timeToLiveInSeconds;
    }

    public void setCacheEntry(@NotNull RecipientsCacheKey recipientsCacheKey, @NotNull RecipientsDTO cacheValue) {
        var cacheKey = recipientsCacheKey.getCacheKey();
        Objects.requireNonNull(cacheValue, "cache.recipients.cacheValue.notNull");

        redisCommands.setex(cacheKey, timeToLiveInSeconds, cacheValue);
    }
}
