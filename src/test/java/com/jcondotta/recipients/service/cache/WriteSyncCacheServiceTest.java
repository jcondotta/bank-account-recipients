package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.QueryParams;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WriteSyncCacheServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final Long DEFAULT_CACHE_ENTRY_EXPIRATION_IN_SECONDS = 3600L;

    private WriteSyncCacheService writeSyncCacheService;

    @Mock
    private RedisCommands<String, RecipientsDTO> redisCommands;

    @Mock
    private RecipientsDTO recipientsDTO;

    @BeforeEach
    void beforeEach() {
        writeSyncCacheService = new WriteSyncCacheService(redisCommands, DEFAULT_CACHE_ENTRY_EXPIRATION_IN_SECONDS);
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldSetCacheEntry_whenParametersAreValid(QueryParams queryParams) {
        var recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        writeSyncCacheService.setCacheEntry(recipientsCacheKey, recipientsDTO);

        verify(redisCommands).setex(anyString(), eq(DEFAULT_CACHE_ENTRY_EXPIRATION_IN_SECONDS), eq(recipientsDTO));
        verifyNoMoreInteractions(redisCommands);
    }

    @Test
    void shouldSetCacheEntry_whenNoQueryParamsIsPassedInCacheKey() {
        var recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL);
        writeSyncCacheService.setCacheEntry(recipientsCacheKey, recipientsDTO);

        verify(redisCommands).setex(anyString(), eq(DEFAULT_CACHE_ENTRY_EXPIRATION_IN_SECONDS), eq(recipientsDTO));
        verifyNoMoreInteractions(redisCommands);
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdCacheKeyIsNull() {
        var exception = assertThrows(NullPointerException.class, () -> {
            new RecipientsCacheKey(null, QueryParams.builder().build());
        });

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("cache.recipients.bankAccountId.notNull"));

        verifyNoInteractions(redisCommands);
    }

    @Test
    void shouldThrowNullPointerException_whenRecipientsDTOIsNull() {
        var recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, QueryParams.builder().build());

        var exception = assertThrows(NullPointerException.class, () -> writeSyncCacheService.setCacheEntry(recipientsCacheKey, null));

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("cache.recipients.cacheValue.notNull"));

        verifyNoInteractions(redisCommands);
    }
}