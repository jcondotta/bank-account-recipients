package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
public class RecipientsCacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientsCacheService.class);

    private WriteSyncCacheService writeSyncCacheService;
    private ReadSyncCacheService readSyncCacheService;

    @Inject
    public RecipientsCacheService(WriteSyncCacheService writeSyncCacheService, ReadSyncCacheService readSyncCacheService) {
        this.writeSyncCacheService = writeSyncCacheService;
        this.readSyncCacheService = readSyncCacheService;
    }

    public void setCacheEntry(QueryRecipientsRequest queryRecipientsRequest, RecipientsDTO recipientsDTO) {
        var recipientsCacheKey = new RecipientsCacheKey(queryRecipientsRequest.bankAccountId(), queryRecipientsRequest.queryParams());

        LOGGER.debug("[BankAccountId={}] Storing recipients in cache with key: {}", queryRecipientsRequest.bankAccountId(), recipientsCacheKey);
        writeSyncCacheService.setCacheEntry(recipientsCacheKey, recipientsDTO);
    }

    public Optional<RecipientsDTO> getCacheEntry(QueryRecipientsRequest queryRecipientsRequest){
        var recipientsCacheKey = new RecipientsCacheKey(queryRecipientsRequest.bankAccountId(), queryRecipientsRequest.queryParams());
        Optional<RecipientsDTO> cachedRecipientsDTO = readSyncCacheService.getCacheEntry(recipientsCacheKey);

        cachedRecipientsDTO.ifPresentOrElse(recipientsDTO -> LOGGER.info("[BankAccountId={}] Cache hit for key: {}", queryRecipientsRequest.bankAccountId(), recipientsCacheKey),
                () -> LOGGER.info("[BankAccountId={}] Cache miss for key: {}", queryRecipientsRequest.bankAccountId(), recipientsCacheKey));

        return cachedRecipientsDTO;
    }
}
