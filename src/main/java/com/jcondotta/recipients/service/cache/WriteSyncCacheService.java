package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.service.dto.RecipientsDTO;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Value;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

@Singleton
public class WriteSyncCacheService {

    private static final Logger logger = LoggerFactory.getLogger(WriteSyncCacheService.class);

    private final RedisCommands<String, RecipientsDTO> redisCommands;
    private final Long cacheEntryExpirationInSeconds;

    @Inject
    public WriteSyncCacheService(RedisCommands<String, RecipientsDTO> redisCommands, @Value("${aws.sqs.endpoint:3600}") Long cacheEntryExpirationInSeconds) {
        this.redisCommands = redisCommands;
        this.cacheEntryExpirationInSeconds = cacheEntryExpirationInSeconds;
    }

    public void setCacheEntry(@NotNull RecipientsCacheKey recipientsCacheKey, @NotNull RecipientsDTO cacheValue) {
        var cacheKey = recipientsCacheKey.getCacheKey();
        cacheValue = Objects.requireNonNull(cacheValue, "cache.recipients.cacheValue.notNull");

        redisCommands.setex(cacheKey, cacheEntryExpirationInSeconds, cacheValue);
        logger.info("RecipientDTO cached with key: {}", cacheKey);
    }
}
