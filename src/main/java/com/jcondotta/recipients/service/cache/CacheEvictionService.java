package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.service.dto.RecipientsDTO;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Singleton
public class CacheEvictionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheEvictionService.class);
    private static final String BANK_ACCOUNT_ID_TEMPLATE = "recipients:bank-account-id:%s:*";

    private final RedisCommands<String, RecipientsDTO> redisCommands;

    public CacheEvictionService(RedisCommands<String, RecipientsDTO> redisCommands) {
        this.redisCommands = redisCommands;
    }

    public void evictCacheEntriesByBankAccountId(UUID bankAccountId){
        LOGGER.info("Invalidating cache entries for bank account ID: {}", bankAccountId);

        String keyPattern = String.format(BANK_ACCOUNT_ID_TEMPLATE, bankAccountId);
        KeyScanCursor<String> keyScanCursor = redisCommands.scan(ScanArgs.Builder.matches(keyPattern));

        for (String key : keyScanCursor.getKeys()) {
            System.out.println("deleting key: " + key);
            redisCommands.del(key);
        }

        LOGGER.info("Completed invalidation for keys matching pattern: {}", keyPattern);
    }
}
