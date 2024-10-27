package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.service.dto.RecipientsDTO;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
public class ReadSyncCacheService {

    private static final Logger logger = LoggerFactory.getLogger(ReadSyncCacheService.class);

    private final RedisCommands<String, RecipientsDTO> redisCommands;

    @Inject
    public ReadSyncCacheService(RedisCommands<String, RecipientsDTO> redisCommands) {
        this.redisCommands = redisCommands;
    }

    public Optional<RecipientsDTO> getCacheEntry(@NotNull RecipientsCacheKey recipientsCacheKey) {
        var cacheKey = recipientsCacheKey.getCacheKey();
        Optional<RecipientsDTO> recipientsDTO = Optional.ofNullable(redisCommands.get(cacheKey));

        recipientsDTO.ifPresentOrElse(dto -> logger.info("Cache hit for key: {}", cacheKey),
                () -> logger.info("Cache miss for key: {}", cacheKey));

        return recipientsDTO;
    }
}
