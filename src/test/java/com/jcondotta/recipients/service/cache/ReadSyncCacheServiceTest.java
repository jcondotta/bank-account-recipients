package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.QueryParams;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadSyncCacheServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @InjectMocks
    private ReadSyncCacheService readSyncCacheService;

    @Mock
    private RedisCommands<String, RecipientsDTO> redisCommands;

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnCacheEntry_whenParametersAreValid(QueryParams queryParams) {
        var recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        readSyncCacheService.getCacheEntry(recipientsCacheKey);

        verify(redisCommands).get(anyString());
        verifyNoMoreInteractions(redisCommands);
    }

    @Test
    void shouldReturnCacheEntry_whenNoQueryParamsIsPassedInCacheKey() {
        var recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL);
        readSyncCacheService.getCacheEntry(recipientsCacheKey);

        verify(redisCommands).get(anyString());
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
}