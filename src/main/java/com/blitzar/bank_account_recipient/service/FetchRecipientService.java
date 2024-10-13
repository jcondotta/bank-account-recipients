package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import com.blitzar.bank_account_recipient.service.query.builder.RecipientQueryConditionalBuilder;
import com.blitzar.bank_account_recipient.service.query.builder.RecipientQueryEnhancedRequestBuilder;
import com.blitzar.bank_account_recipient.service.query.parser.RecipientPageParser;
import com.blitzar.bank_account_recipient.service.request.QueryRecipientsRequest;
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

    private static final Logger logger = LoggerFactory.getLogger(FetchRecipientService.class);

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
        logger.info("[BankAccountId={}] Fetching recipients with params: {}", queryRecipientsRequest.bankAccountId(), queryRecipientsRequest.queryParams());

        var constraintViolations = validator.validate(queryRecipientsRequest);
        if (!constraintViolations.isEmpty()) {
            logger.warn("[BankAccountId={}] Constraint violations found: {}", queryRecipientsRequest.bankAccountId(), constraintViolations);

            throw new ConstraintViolationException(constraintViolations);
        }

        var queryRequest = buildQueryEnhancedRequest(queryRecipientsRequest);
        Page<Recipient> recipientsPage = dynamoDbTable.query(queryRequest)
                .stream()
                .findFirst().orElse(null);

        var recipientsDTO = recipientPageParser.parse(recipientsPage);
//        logger.info("[BankAccountId={}] {} recipient(s) found", queryRecipientsRequest.bankAccountId(), recipientsDTO.count());

        return recipientsDTO;
    }

    private QueryEnhancedRequest buildQueryEnhancedRequest(QueryRecipientsRequest queryRecipientsRequest){
        var recipientQueryConditionalBuilder = new RecipientQueryConditionalBuilder();
        return new RecipientQueryEnhancedRequestBuilder(recipientQueryConditionalBuilder).build(queryRecipientsRequest);
    }
}
