package com.jcondotta.recipients.service.cache;


import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.QueryParams;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheEvictionServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @InjectMocks
    private CacheEvictionService cacheEvictionService;

    @Mock
    private RedisCommands<String, RecipientsDTO> redisCommands;

    @Mock
    private KeyScanCursor<String> keyScanCursor;

    @Test
    void shouldEvictNoCacheEntries_whenCacheHasNoBankAccountIdEntries(){
        when(keyScanCursor.getKeys()).thenReturn(List.of());
        when(redisCommands.scan(any(ScanArgs.class))).thenReturn(keyScanCursor);

        cacheEvictionService.evictCacheEntriesByBankAccountId(BANK_ACCOUNT_ID_BRAZIL);

        verify(redisCommands).scan(any(ScanArgs.class));
        verifyNoMoreInteractions(redisCommands);
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldEvictCacheEntry_whenSingleEntryIsRelatedToBankAccountId(QueryParams queryParams){
        var recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        var cacheKey = recipientsCacheKey.getCacheKey();

        when(redisCommands.scan(any(ScanArgs.class))).thenReturn(keyScanCursor);
        when(keyScanCursor.getKeys()).thenReturn(List.of(cacheKey));

        cacheEvictionService.evictCacheEntriesByBankAccountId(BANK_ACCOUNT_ID_BRAZIL);

        verify(redisCommands).scan(any(ScanArgs.class));
        verify(redisCommands).del(cacheKey);
        verifyNoMoreInteractions(redisCommands);
    }

    @Test
    void shouldEvictCacheEntries_whenMultipleEntriesAreRelatedToBankAccountId(){
        var recipientsCacheKey1 = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, QueryParams.builder()
                .withRecipientName("Jef")
                .build());

        var recipientsCacheKey2 = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, QueryParams.builder()
                .withLimit(15)
                .build());

        when(redisCommands.scan(any(ScanArgs.class))).thenReturn(keyScanCursor);
        when(keyScanCursor.getKeys())
                .thenReturn(List.of(recipientsCacheKey1.getCacheKey(), recipientsCacheKey2.getCacheKey()));

        cacheEvictionService.evictCacheEntriesByBankAccountId(BANK_ACCOUNT_ID_BRAZIL);

        verify(redisCommands).scan(any(ScanArgs.class));
        verify(redisCommands).del(recipientsCacheKey1.getCacheKey());
        verify(redisCommands).del(recipientsCacheKey2.getCacheKey());
        verifyNoMoreInteractions(redisCommands);
    }
}
