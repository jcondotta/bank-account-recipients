package com.jcondotta.recipients.service.query.builder;

import com.jcondotta.recipients.helper.QueryParamsBuilder;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RecipientQueryEnhancedRequestBuilderTest {

    private static final int DEFAULT_PAGE_LIMIT = 10;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private final RecipientQueryConditionalBuilder queryConditionalBuilder = new RecipientQueryConditionalBuilder();

    private RecipientQueryEnhancedRequestBuilder queryEnhancedRequestBuilder;

    private QueryRecipientsRequest queryRecipientsRequest;
    private QueryParams queryParams;

    @BeforeEach
    void beforeEach() {
        queryEnhancedRequestBuilder = new RecipientQueryEnhancedRequestBuilder(queryConditionalBuilder);
    }

    @Test
    void shouldReturnQueryRequest_whenBankAccountIdIsNotNull() {
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL);

        var queryEnhancedRequest = queryEnhancedRequestBuilder.build(queryRecipientsRequest);

        assertThat(queryEnhancedRequest.queryConditional()).isNotNull();
        assertThat(queryEnhancedRequest.limit()).isEqualTo(DEFAULT_PAGE_LIMIT);
        assertThat(queryEnhancedRequest.exclusiveStartKey()).isNull();
    }

    @Test
    void shouldReturnQueryRequest_whenRecipientNameIsNotEmpty() {
        queryParams = new QueryParamsBuilder().withRecipientName(RECIPIENT_NAME_JEFFERSON).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var queryEnhancedRequest = queryEnhancedRequestBuilder.build(queryRecipientsRequest);

        assertThat(queryEnhancedRequest.queryConditional()).isNotNull();
        assertThat(queryEnhancedRequest.limit()).isEqualTo(DEFAULT_PAGE_LIMIT);
        assertThat(queryEnhancedRequest.exclusiveStartKey()).isNull();
    }

    @Test
    void shouldReturnQueryRequest_whenLimitIsNotDefault() {
        var pageLimit = 100;
        queryParams = new QueryParamsBuilder().withLimit(pageLimit).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        QueryEnhancedRequest queryEnhancedRequest = queryEnhancedRequestBuilder.build(queryRecipientsRequest);

        assertThat(queryEnhancedRequest.queryConditional()).isNotNull();
        assertThat(queryEnhancedRequest.limit()).isEqualTo(pageLimit);
        assertThat(queryEnhancedRequest.exclusiveStartKey()).isNull();
    }

    @Test
    void shouldReturnQueryRequest_whenLastEvaluatedKeyIsNotNull() {
        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        queryParams = new QueryParamsBuilder().withLastEvaluatedKey(lastEvaluatedKey).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        QueryEnhancedRequest queryEnhancedRequest = queryEnhancedRequestBuilder.build(queryRecipientsRequest);

        assertThat(queryEnhancedRequest.queryConditional()).isNotNull();
        assertThat(queryEnhancedRequest.limit()).isEqualTo(DEFAULT_PAGE_LIMIT);
        assertThat(queryEnhancedRequest.exclusiveStartKey()).hasSize(2);

        assertThat(queryEnhancedRequest.exclusiveStartKey())
                .containsEntry("bankAccountId", AttributeValue.fromS(BANK_ACCOUNT_ID_BRAZIL.toString()))
                .containsEntry("recipientName", AttributeValue.fromS(RECIPIENT_NAME_JEFFERSON));
    }
}