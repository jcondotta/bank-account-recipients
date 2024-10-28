package com.jcondotta.recipients.service.query.builder;

import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class RecipientQueryEnhancedRequestBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientQueryEnhancedRequestBuilder.class);

    private static final int DEFAULT_PAGE_LIMIT = 10;

    private RecipientQueryConditionalBuilder recipientQueryConditionalBuilder;

    public RecipientQueryEnhancedRequestBuilder(RecipientQueryConditionalBuilder recipientQueryConditionalBuilder) {
        this.recipientQueryConditionalBuilder = recipientQueryConditionalBuilder;
    }

    public QueryEnhancedRequest build(QueryRecipientsRequest queryRecipientsRequest) {
        LOGGER.info("[BankAccountId={}] Building query for DynamoDB with parameters: {}",
                queryRecipientsRequest.bankAccountId(), queryRecipientsRequest.queryParams());

        final var queryConditional = recipientQueryConditionalBuilder.build(queryRecipientsRequest);
        final var limit = queryRecipientsRequest.queryParams().limit().orElse(DEFAULT_PAGE_LIMIT);

        LOGGER.debug("[BankAccountId={}] Query limit set to: {}", queryRecipientsRequest.bankAccountId(), limit);

        final Map<String, AttributeValue> exclusiveStartKey = buildExclusiveStartKey(queryRecipientsRequest.queryParams());

        return QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .exclusiveStartKey(exclusiveStartKey)
                .limit(limit)
                .build();
    }

    private Map<String, AttributeValue> buildExclusiveStartKey(QueryParams queryParams){
        return queryParams.lastEvaluatedKey()
                .map(LastEvaluatedKey::toExclusiveStartKey)
                .orElse(null);
    }
}