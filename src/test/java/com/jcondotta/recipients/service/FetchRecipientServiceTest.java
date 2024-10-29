package com.jcondotta.recipients.service;

import com.jcondotta.recipients.argument_provider.validation.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.security.ThreatInputArgumentProvider;
import com.jcondotta.recipients.factory.ValidatorTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.cache.RecipientsCacheService;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchRecipientServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @Mock
    private DynamoDbFetchRecipientService dynamoDbRecipientService;

    private FetchRecipientService fetchRecipientService;

    private QueryRecipientsRequest queryRecipientsRequest;

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

        var dbQueriedRecipientsDTO = fetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(recipientsCacheService).getCacheEntry(queryRecipientsRequest);
        verify(dynamoDbRecipientService).findRecipients(queryRecipientsRequest);
        verify(recipientsCacheService).setCacheEntry(queryRecipientsRequest, dbQueriedRecipientsDTO);
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnCachedList_whenCacheEntryExistsForQueryRecipientRequestKey(QueryParams queryParams) {
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        when(recipientsCacheService.getCacheEntry(queryRecipientsRequest)).thenReturn(Optional.of(recipientsDTO));

        fetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(recipientsCacheService).getCacheEntry(queryRecipientsRequest);
        verifyNoMoreInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @Test
    void shouldNotThrowException_whenAllParamsAreProvided() {
        var queryParams = QueryParams.builder()
                .withRecipientName(RECIPIENT_NAME_JEFFERSON)
                .withLimit(20)
                .withLastEvaluatedKey(new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON))
                .build();

        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        fetchRecipientService.findRecipients(queryRecipientsRequest);

        assertThat(queryRecipientsRequest.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
        assertThat(queryRecipientsRequest.queryParams().recipientName()).isEqualTo(queryParams.recipientName());
        assertThat(queryRecipientsRequest.queryParams().limit()).isEqualTo(queryParams.limit());
        assertThat(queryRecipientsRequest.queryParams().lastEvaluatedKey())
                .usingRecursiveAssertion()
                .isEqualTo(queryParams.lastEvaluatedKey());
    }

    @Test
    void shouldThrowConstraintViolationException_whenQueryParamsRecipientNameExceeds50Characters() {
        final var veryLongRecipientName = "J".repeat(51);
        final var queryParams = QueryParams.builder().withRecipientName(veryLongRecipientName).build();

        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenQueryParamsRecipientNameIsMalicious(String invalidRecipientName) {
        final var queryParams = QueryParams.builder().withRecipientName(invalidRecipientName).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenQueryParamsLimitIsBelow1() {
        var queryParams = QueryParams.builder().withLimit(0).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenQueryParamsLimitExceeds20() {
        var queryParams = QueryParams.builder().withLimit(21).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenQueryParamsLastEvaluatedKeyHasNullBankAccountId() {
        var queryParams = QueryParams.builder().withLastEvaluatedKey(new LastEvaluatedKey(null, RECIPIENT_NAME_JEFFERSON)).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenQueryParamsLastEvaluatedKeyHasBlankRecipientName(String blankRecipientName) {
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, blankRecipientName)).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenQueryParamsLastEvaluatedKeyRecipientNameExceeds50Characters() {
        final var veryLongRecipientName = "J".repeat(51);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, veryLongRecipientName)).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenQueryParamsLastEvaluatedKeyRecipientNameIsMalicious(String maliciousRecipientName) {
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, maliciousRecipientName)).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientsCacheService, dynamoDbRecipientService);
    }
}