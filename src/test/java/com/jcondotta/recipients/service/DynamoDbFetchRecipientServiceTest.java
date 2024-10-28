package com.jcondotta.recipients.service;

import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.query.parser.RecipientPageParser;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamoDbFetchRecipientServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @InjectMocks
    private DynamoDbFetchRecipientService dynamoDbFetchRecipientService;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Mock
    private PageIterable<Recipient> pageIterable;

    @Mock
    private Page<Recipient> pageRecipient;

    private QueryRecipientsRequest queryRecipientsRequest;

    @Mock
    private RecipientPageParser recipientPageParser;

    @Mock
    private RecipientsDTO recipientsDTO;

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnList_whenQueryRecipientRequestIsValid(QueryParams queryParams) {
        when(dynamoDbTable.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(pageRecipient));
        when(recipientPageParser.parse(pageRecipient)).thenReturn(recipientsDTO);

        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        dynamoDbFetchRecipientService.findRecipients(queryRecipientsRequest);

        verify(dynamoDbTable).query(Mockito.any(QueryEnhancedRequest.class));
        verify(recipientPageParser).parse(pageRecipient);
        verifyNoMoreInteractions(recipientPageParser, dynamoDbTable);
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () -> {
            queryRecipientsRequest = new QueryRecipientsRequest(null);
            dynamoDbFetchRecipientService.findRecipients(queryRecipientsRequest);
        });

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("query.recipients.bankAccountId.notNull"));

        verify(recipientPageParser, never()).parse(pageRecipient);
        verifyNoMoreInteractions(recipientPageParser, dynamoDbTable);
    }
}