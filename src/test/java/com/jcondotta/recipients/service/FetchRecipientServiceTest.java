package com.jcondotta.recipients.service;

import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.factory.ValidatorTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.service.cache.RecipientsCacheService;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.query.parser.RecipientPageParser;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchRecipientServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @Mock
    private DynamoDbFetchRecipientService dynamoDbRecipientService;

    @Mock
    private PageIterable<Recipient> pageIterable;

    @Mock
    private Page<Recipient> pageRecipient;

    private FetchRecipientService fetchRecipientService;

    private QueryParams queryParams;
    private QueryRecipientsRequest queryRecipientsRequest;

    @Mock
    private RecipientPageParser recipientPageParser;

    @Mock
    private RecipientsDTO recipientsDTO;

    @Mock
    private RecipientsCacheService recipientsCacheService;

    @BeforeEach
    void beforeEach() {
        fetchRecipientService = new FetchRecipientService(dynamoDbRecipientService, recipientsCacheService, VALIDATOR);
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnDbQueriedList_whenNoCacheEntryExistsForQueryRecipientRequestIsKey(QueryParams queryParams) {
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var recipientsDTO = fetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(recipientsCacheService).getCacheEntry(eq(queryRecipientsRequest));
        verify(dynamoDbRecipientService).findRecipients(eq(queryRecipientsRequest));
        verify(recipientsCacheService).setCacheEntry(eq(queryRecipientsRequest), eq(recipientsDTO));
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnCachedList_whenCacheEntryExistsForQueryRecipientRequestKey(QueryParams queryParams) {
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        when(recipientsCacheService.getCacheEntry(queryRecipientsRequest)).thenReturn(Optional.of(recipientsDTO));

        fetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(recipientsCacheService).getCacheEntry(eq(queryRecipientsRequest));
        verifyNoMoreInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @Test
    void shouldThrowNullPointerExceptionException_whenBankAccountIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () -> {
            queryRecipientsRequest = new QueryRecipientsRequest(null);
            fetchRecipientService.findRecipients(queryRecipientsRequest);
        });

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("query.recipients.bankAccountId.notNull"));

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }
}