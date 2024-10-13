package com.blitzar.bank_account_recipient.service.query.builder;

import com.blitzar.bank_account_recipient.service.request.QueryParams;
import com.blitzar.bank_account_recipient.service.request.QueryRecipientsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

public class RecipientQueryEnhancedRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(RecipientQueryEnhancedRequestBuilder.class);

    private static final int DEFAULT_PAGE_LIMIT = 10;

    private RecipientQueryConditionalBuilder recipientQueryConditionalBuilder;

    public RecipientQueryEnhancedRequestBuilder(RecipientQueryConditionalBuilder recipientQueryConditionalBuilder) {
        this.recipientQueryConditionalBuilder = recipientQueryConditionalBuilder;
    }

    public QueryEnhancedRequest build(QueryRecipientsRequest queryRecipientsRequest) {
        QueryConditional queryConditional = recipientQueryConditionalBuilder.build(queryRecipientsRequest);

        final var queryParams = queryRecipientsRequest.queryParams();
        final var limit = queryParams.flatMap(QueryParams::limit).orElse(DEFAULT_PAGE_LIMIT);

        logger.debug("[BankAccountId={}] Query limit set to: {}", queryRecipientsRequest.bankAccountId(), limit);

        final Map<String, AttributeValue> exclusiveStartKey = buildExclusiveStartKey(queryParams);

        return QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .exclusiveStartKey(exclusiveStartKey)
                .limit(limit)
                .build();
    }

    public Map<String, AttributeValue> buildExclusiveStartKey(Optional<QueryParams> queryParams){
        return queryParams
                .map(QueryParams::getExclusiveStartKey)
                .orElse(null);
    }
}