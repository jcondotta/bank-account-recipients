package com.jcondotta.recipients.service;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.query.builder.RecipientQueryConditionalBuilder;
import com.jcondotta.recipients.service.query.builder.RecipientQueryEnhancedRequestBuilder;
import com.jcondotta.recipients.service.query.parser.RecipientPageParser;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Singleton
public class FetchRecipientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchRecipientService.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;
    private final RecipientPageParser recipientPageParser;
    private final Validator validator;

    @Inject
    public FetchRecipientService(DynamoDbTable<Recipient> dynamoDbTable, RecipientPageParser recipientPageParser, Validator validator) {
        this.dynamoDbTable = dynamoDbTable;
        this.recipientPageParser = recipientPageParser;
        this.validator = validator;
    }

    public RecipientsDTO findRecipients(@NotNull QueryRecipientsRequest queryRecipientsRequest) {
        LOGGER.info("[BankAccountId={}] Fetching recipients with params: {}", queryRecipientsRequest.bankAccountId(), queryRecipientsRequest.queryParams());

        var constraintViolations = validator.validate(queryRecipientsRequest);
        if (!constraintViolations.isEmpty()) {
            LOGGER.warn("[BankAccountId={}] Constraint violations found: {}", queryRecipientsRequest.bankAccountId(), constraintViolations);

            throw new ConstraintViolationException(constraintViolations);
        }

        var queryRequest = buildQueryEnhancedRequest(queryRecipientsRequest);
        Page<Recipient> recipientsPage = dynamoDbTable.query(queryRequest)
                .stream()
                .findFirst().orElse(null);

        var recipientsDTO = recipientPageParser.parse(recipientsPage);
        LOGGER.info("[BankAccountId={}] {} recipient(s) found", queryRecipientsRequest.bankAccountId(), recipientsDTO.count());

        return recipientsDTO;
    }

    private QueryEnhancedRequest buildQueryEnhancedRequest(QueryRecipientsRequest queryRecipientsRequest){
        var recipientQueryConditionalBuilder = new RecipientQueryConditionalBuilder();
        return new RecipientQueryEnhancedRequestBuilder(recipientQueryConditionalBuilder).build(queryRecipientsRequest);
    }
}
