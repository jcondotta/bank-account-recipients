package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.service.dto.RecipientsDTO;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@Singleton
public class ReadSyncCacheService {

    private final RedisCommands<String, RecipientsDTO> redisCommands;

    @Inject
    public ReadSyncCacheService(RedisCommands<String, RecipientsDTO> redisCommands) {
        this.redisCommands = redisCommands;
    }

    public Optional<RecipientsDTO> getCacheEntry(@NotNull RecipientsCacheKey recipientsCacheKey) {
        var cacheKey = recipientsCacheKey.getCacheKey();

        return Optional.ofNullable(redisCommands.get(cacheKey));
    }
}
