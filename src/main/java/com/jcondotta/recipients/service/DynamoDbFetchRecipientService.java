package com.jcondotta.recipients.service;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.query.builder.RecipientQueryConditionalBuilder;
import com.jcondotta.recipients.service.query.builder.RecipientQueryEnhancedRequestBuilder;
import com.jcondotta.recipients.service.query.parser.RecipientPageParser;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Singleton
public class DynamoDbFetchRecipientService {

    private final DynamoDbTable<Recipient> dynamoDbTable;
    private final RecipientPageParser recipientPageParser;

    @Inject
    public DynamoDbFetchRecipientService(DynamoDbTable<Recipient> dynamoDbTable, RecipientPageParser recipientPageParser) {
        this.dynamoDbTable = dynamoDbTable;
        this.recipientPageParser = recipientPageParser;
    }

    public RecipientsDTO findRecipients(@NotNull QueryRecipientsRequest queryRecipientsRequest) {
        final var queryEnhancedRequest = buildQueryEnhancedRequest(queryRecipientsRequest);
        return dynamoDbTable.query(queryEnhancedRequest)
                .stream()
                .findFirst()
                .map(recipientPage -> recipientPageParser.parse(recipientPage))
                .orElseThrow();
    }

    private QueryEnhancedRequest buildQueryEnhancedRequest(QueryRecipientsRequest queryRecipientsRequest){
        var recipientQueryConditionalBuilder = new RecipientQueryConditionalBuilder();

        return new RecipientQueryEnhancedRequestBuilder(recipientQueryConditionalBuilder)
                .build(queryRecipientsRequest);
    }
}
