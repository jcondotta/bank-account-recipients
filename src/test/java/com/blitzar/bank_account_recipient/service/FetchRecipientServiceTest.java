package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.factory.ValidatorTestFactory;
import com.blitzar.bank_account_recipient.helper.QueryParamsBuilder;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import com.blitzar.bank_account_recipient.service.query.parser.RecipientPageParser;
import com.blitzar.bank_account_recipient.service.request.LastEvaluatedKey;
import com.blitzar.bank_account_recipient.service.request.QueryParams;
import com.blitzar.bank_account_recipient.service.request.QueryRecipientsRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private RecipientsDTO recipientsDTO;

    @BeforeEach
    void beforeEach(){
        fetchRecipientService = new FetchRecipientService(dynamoDbTable, recipientPageParser, VALIDATOR);
    }

    @Test
    void shouldReturnList_whenBankAccountIdIsNotNull() {
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL);

        when(dynamoDbTable.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(pageRecipient));
        when(recipientPageParser.parse(pageRecipient)).thenReturn(recipientsDTO);

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
        when(recipientPageParser.parse(pageRecipient)).thenReturn(recipientsDTO);

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
        when(recipientPageParser.parse(pageRecipient)).thenReturn(recipientsDTO);

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
        when(recipientPageParser.parse(pageRecipient)).thenReturn(recipientsDTO);

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
}