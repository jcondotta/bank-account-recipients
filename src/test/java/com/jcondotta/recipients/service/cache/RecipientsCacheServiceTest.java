package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientsCacheServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    private static final String FIXED_CACHE_KEY = "recipients:bank-account-id:01920bff-1338-7efd-ade6-e9128debe5d4:" +
            "query-params:2c013149ead6d11268ae479e1e3ef90bce4d3c62cfbdbe6545b24263ceeea9d5";

    @InjectMocks
    private RecipientsCacheService recipientsCacheService;

    @Mock
    private WriteSyncCacheService writeSyncCacheService;

    @Mock
    private ReadSyncCacheService readSyncCacheService;

    @Mock
    private QueryRecipientsRequest queryRecipientsRequest;

    @Mock
    private RecipientsDTO recipientsDTO;

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldSetCacheEntry_whenQueryRecipientsRequestIsValid(QueryParams queryParams) {
        when(queryRecipientsRequest.bankAccountId()).thenReturn(BANK_ACCOUNT_ID_BRAZIL);
        when(queryRecipientsRequest.queryParams()).thenReturn(queryParams);

        recipientsCacheService.setCacheEntry(queryRecipientsRequest, recipientsDTO);

        verify(writeSyncCacheService).setCacheEntry(any(RecipientsCacheKey.class), eq(recipientsDTO));

        verifyNoMoreInteractions(writeSyncCacheService);
        verifyNoInteractions(readSyncCacheService);
    }

    @Test
    void shouldThrowNullPointerException_whenSetCacheAndQueryRecipientsRequestHasNullBankAccountId() {
        when(queryRecipientsRequest.bankAccountId()).thenReturn(null);

        var exception = assertThrows(NullPointerException.class, () -> {
            recipientsCacheService.setCacheEntry(queryRecipientsRequest, recipientsDTO);
        });

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("cache.recipients.bankAccountId.notNull"));

        verifyNoInteractions(writeSyncCacheService, readSyncCacheService);
    }

    @Test
    void shouldThrowNullPointerException_whenRecipientsDTOIsNull() {
        when(queryRecipientsRequest.bankAccountId()).thenReturn(BANK_ACCOUNT_ID_BRAZIL);

        recipientsCacheService.setCacheEntry(queryRecipientsRequest, null);

        verify(writeSyncCacheService).setCacheEntry(any(RecipientsCacheKey.class), eq(null));
        verifyNoInteractions(readSyncCacheService);
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldGetCacheEntry_whenQueryRecipientsRequestIsValid(QueryParams queryParams) {
        when(queryRecipientsRequest.bankAccountId()).thenReturn(BANK_ACCOUNT_ID_BRAZIL);
        when(queryRecipientsRequest.queryParams()).thenReturn(queryParams);

        recipientsCacheService.getCacheEntry(queryRecipientsRequest);

        verify(readSyncCacheService).getCacheEntry(any(RecipientsCacheKey.class));
        verifyNoMoreInteractions(readSyncCacheService);
        verifyNoInteractions(writeSyncCacheService);
    }

    @Test
    void shouldThrowNullPointerException_whenGetCacheAndQueryRecipientsRequestHasNullBankAccountId() {
        when(queryRecipientsRequest.bankAccountId()).thenReturn(null);

        var exception = assertThrows(NullPointerException.class, () -> {
            recipientsCacheService.getCacheEntry(queryRecipientsRequest);
        });

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("cache.recipients.bankAccountId.notNull"));

        verifyNoInteractions(readSyncCacheService, writeSyncCacheService);
    }
}