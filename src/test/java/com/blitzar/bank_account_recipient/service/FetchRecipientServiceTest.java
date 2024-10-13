package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.factory.ValidatorTestFactory;
import com.blitzar.bank_account_recipient.helper.QueryParamsBuilder;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.query.parser.RecipientPageParser;
import com.blitzar.bank_account_recipient.service.request.LastEvaluatedKey;
import com.blitzar.bank_account_recipient.service.request.QueryParams;
import com.blitzar.bank_account_recipient.service.request.QueryRecipientsRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchRecipientServiceTest {

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Mock
    private PageIterable<Recipient> pageIterable;

    @Mock
    private Page<Recipient> pageRecipient;

    private FetchRecipientService fetchRecipientService;

    private QueryParams queryParams;
    private QueryRecipientsRequest queryRecipientsRequest;

    @Mock
    private RecipientPageParser recipientPageParser;

    @BeforeEach
    void beforeEach(){
        fetchRecipientService = new FetchRecipientService(dynamoDbTable, recipientPageParser, VALIDATOR);
    }

    @Test
    void shouldReturnList_whenBankAccountIdIsNotNull() {
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL);

        when(dynamoDbTable.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(pageRecipient));

        fetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(recipientPageParser).parse(pageRecipient);
    }

    @Test
    void shouldReturnListWithRecipientNamePrefixMatching_whenRecipientNamePrefixIsNotEmpty() {
        final var recipientNamePrefix = "Je";
        queryParams = new QueryParamsBuilder().withRecipientName(recipientNamePrefix).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        when(dynamoDbTable.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(pageRecipient));

        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        fetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(recipientPageParser).parse(pageRecipient);
    }

    @Test
    void shouldReturnList_whenLimitIsNotDefault() {
        var pageLimit = 100;
        queryParams = new QueryParamsBuilder().withLimit(pageLimit).build();

        when(dynamoDbTable.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(pageRecipient));

        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        fetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(recipientPageParser).parse(pageRecipient);
    }

    @Test
    void shouldReturnList_whenLastEvaluatedKeyIsNotNull() {
        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        queryParams = new QueryParamsBuilder().withLastEvaluatedKey(lastEvaluatedKey).build();

        when(dynamoDbTable.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(pageRecipient));

        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        fetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(recipientPageParser).parse(pageRecipient);
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        queryRecipientsRequest = new QueryRecipientsRequest(null);

        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
                });

        verify(recipientPageParser, never()).parse(pageRecipient);
    }

    @Test
    @Disabled(value = "Make it better in future")
    void shouldThrowConstraintViolationException_whenLastEvaluatedKeyMissesBankAccountId() {
//        var lastEvaluatedKey = new LastEvaluatedKey(null, RECIPIENT_NAME_JEFFERSON);
//        queryParams = new QueryParamsBuilder().withLastEvaluatedKey(lastEvaluatedKey).build();
//
//        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
//
//        verify(recipientPageParser, never()).parse(pageRecipient);
    }

    @Test
    @Disabled(value = "Make it better in future")
    void shouldThrowConstraintViolationException_whenLastEvaluatedKeyMissesRecipientName() {
//        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, null);
//        queryParams = new QueryParamsBuilder().withLastEvaluatedKey(lastEvaluatedKey).build();
//
//        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
//
//        verify(recipientPageParser, never()).parse(pageRecipient);
    }

//
//    @Test
//    void shouldReturnPagedRecipientList_whenLastEvaluatedKeyIsProvided() {
//        var previousPageLastRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);
//        var recipientPatrizio = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.PATRIZIO);
//        var recipientVirginio = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.VIRGINIO);
//
//        var recipientsPage = List.of(recipientPatrizio, recipientVirginio);
//        when(pageRecipient.items()).thenReturn(recipientsPage);
//
//        queryParams = new QueryParamsBuilder()
//                .withLimit(recipientsPage.size())
//                .withLastEvaluatedKey(new LastEvaluatedKey(previousPageLastRecipient.getBankAccountId(), previousPageLastRecipient.getRecipientName()))
//                .build();
//
//        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, Optional.of(queryParams));
//
//        fetchRecipientService.findRecipients(queryRecipientsRequest);
//        verify(recipientPageParser).parse(pageRecipient);
//    }
//
//    @Test
//    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
//        Mockito.reset(dynamoDbTable);
//        Mockito.reset(pageIterable);
//        queryRecipientsRequest = new QueryRecipientsRequest(null, Optional.empty());
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> fetchRecipientService.findRecipients(queryRecipientsRequest));
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("recipient.bankAccountId.notNull");
//                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
//                });
//
//        verify(recipientPageParser, never()).parse(pageRecipient);
//    }
//
//    @Test
//    void shouldReturnEmptyList_whenBankAccountIdIsNonExistent() {
//        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, Optional.empty());
//
//        fetchRecipientService.findRecipients(queryRecipientsRequest);
//
//        verify(recipientPageParser).parse(pageRecipient);
//    }
//
//    @Test
//    void shouldReturnEmptyList_whenNoRecipientsMatchRecipientNamePrefix(){
//        final var nonExistentPrefixRecipientName = "Z";
//
//        queryParams = new QueryParamsBuilder().withRecipientName(nonExistentPrefixRecipientName).build();
//        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, Optional.of(queryParams));
//
//        when(pageRecipient.items()).thenReturn(Collections.emptyList());
//
//        fetchRecipientService.findRecipients(queryRecipientsRequest);
//
//        verify(recipientPageParser).parse(pageRecipient);
//    }
}