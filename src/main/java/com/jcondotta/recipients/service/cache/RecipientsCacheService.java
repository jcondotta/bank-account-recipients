package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class RecipientsCacheService {

    private WriteSyncCacheService writeSyncCacheService;
    private ReadSyncCacheService readSyncCacheService;

    @Inject
    public RecipientsCacheService(WriteSyncCacheService writeSyncCacheService, ReadSyncCacheService readSyncCacheService) {
        this.writeSyncCacheService = writeSyncCacheService;
        this.readSyncCacheService = readSyncCacheService;
    }

    public void setCacheEntry(QueryRecipientsRequest queryRecipientsRequest, RecipientsDTO recipientsDTO) {
        var recipientsCacheKey = new RecipientsCacheKey(queryRecipientsRequest.bankAccountId(), queryRecipientsRequest.queryParams());
        writeSyncCacheService.setCacheEntry(recipientsCacheKey, recipientsDTO);
    }

    public Optional<RecipientsDTO> getCacheEntry(QueryRecipientsRequest queryRecipientsRequest){
        var recipientsCacheKey = new RecipientsCacheKey(queryRecipientsRequest.bankAccountId(), queryRecipientsRequest.queryParams());
        return readSyncCacheService.getCacheEntry(recipientsCacheKey);
    }
}
