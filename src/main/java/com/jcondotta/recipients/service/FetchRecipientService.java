package com.jcondotta.recipients.service;

import com.jcondotta.recipients.service.cache.RecipientsCacheService;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FetchRecipientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchRecipientService.class);

    private final DynamoDbFetchRecipientService dynamoDbFetchRecipientService;
    private final RecipientsCacheService recipientsCacheService;
    private final Validator validator;

    @Inject
    public FetchRecipientService(DynamoDbFetchRecipientService dynamoDbFetchRecipientService, RecipientsCacheService recipientsCacheService,  Validator validator) {
        this.dynamoDbFetchRecipientService = dynamoDbFetchRecipientService;
        this.recipientsCacheService = recipientsCacheService;
        this.validator = validator;
    }

    public RecipientsDTO findRecipients(@NotNull QueryRecipientsRequest queryRecipientsRequest) {
        LOGGER.info("[BankAccountId={}] Fetching recipients with params: {}", queryRecipientsRequest.bankAccountId(), queryRecipientsRequest.queryParams());

        var constraintViolations = validator.validate(queryRecipientsRequest);
        if (!constraintViolations.isEmpty()) {
            LOGGER.warn("[BankAccountId={}] Constraint violations found: {}", queryRecipientsRequest.bankAccountId(), constraintViolations);

            throw new ConstraintViolationException(constraintViolations);
        }

        return recipientsCacheService.getCacheEntry(queryRecipientsRequest).orElseGet(() -> {
            LOGGER.info("[BankAccountId={}] Cache miss. Fetching from DynamoDB.", queryRecipientsRequest.bankAccountId());

            var recipientsDTO = dynamoDbFetchRecipientService.findRecipients(queryRecipientsRequest);
            recipientsCacheService.setCacheEntry(queryRecipientsRequest, recipientsDTO);

            return recipientsDTO;

//                LOGGER.info("[BankAccountId={}] Successfully fetched and cached recipients.", queryRecipientsRequest.bankAccountId());
        });
    }
}
