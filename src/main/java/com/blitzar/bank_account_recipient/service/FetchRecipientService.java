package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import com.blitzar.bank_account_recipient.service.request.LastEvaluatedKey;
import com.blitzar.bank_account_recipient.service.request.QueryParams;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FetchRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(FetchRecipientService.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;
    private final Validator validator;

    @Inject
    public FetchRecipientService(DynamoDbTable<Recipient> dynamoDbTable, Validator validator) {
        this.dynamoDbTable = dynamoDbTable;
        this.validator = validator;
    }

    public RecipientsDTO findRecipients(@NotNull final UUID bankAccountId, @NotNull final QueryParams queryParams) {
        logger.info("[BankAccountId={}] Fetching recipients with params: {}", bankAccountId, queryParams);

        var constraintViolations = validator.validate(queryParams);
        if (!constraintViolations.isEmpty()) {
            logger.warn("[BankAccountId={}] Constraint violations found: {}", bankAccountId, constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }

        var queryConditional = buildQueryConditional(bankAccountId, queryParams);
        logger.debug("[BankAccountId={}] Built query conditional: {}", bankAccountId, queryConditional);

        var queryRequest = buildQueryEnhancedRequest(queryConditional, queryParams);
        logger.debug("[BankAccountId={}] Built query request: {}", bankAccountId, queryRequest);

        Optional<Page<Recipient>> optRecipientsPage = dynamoDbTable.query(queryRequest)
                .stream().findFirst();

        if (optRecipientsPage.isPresent()) {
            var recipientsPage = optRecipientsPage.get();
            var recipients = recipientsPage.items().stream()
                    .map(RecipientDTO::new)
                    .toList();

            logger.info("[BankAccountId={}] {} recipient(s) found", bankAccountId, recipients.size());

            var lastEvaluatedKey = buildLastEvaluatedKey(recipientsPage);
            return new RecipientsDTO(recipients, recipients.size(), lastEvaluatedKey);
        }

        logger.info("[BankAccountId={}] No recipients found.", bankAccountId);
        return new RecipientsDTO(Collections.emptyList(), 0, null);
    }

    private QueryConditional buildQueryConditional(UUID bankAccountId, QueryParams queryParams) {
        var recipientKeyBuilder = Key.builder().partitionValue(bankAccountId.toString());

        if (queryParams.recipientName().isPresent()) {
            recipientKeyBuilder.sortValue(queryParams.recipientName().get());
            logger.debug("[BankAccountId={}] Using recipient name for sorting: {}", bankAccountId, queryParams.recipientName().get());
            return QueryConditional.sortBeginsWith(recipientKeyBuilder.build());
        }

        logger.debug("[BankAccountId={}] No recipient name provided; using partition key only.", bankAccountId);
        return QueryConditional.keyEqualTo(recipientKeyBuilder.build());
    }

    private QueryEnhancedRequest buildQueryEnhancedRequest(final QueryConditional queryConditional, final QueryParams queryParams) {
        final var limit = queryParams.limit().orElse(10);
        logger.debug("[BankAccountId={}] Query limit set to: {}", queryParams);

        Map<String, AttributeValue> exclusiveStartKey = queryParams.lastEvaluatedKey()
                .map(lastEvaluatedKey -> lastEvaluatedKey.toExclusiveStartKey())
                .orElse(null);

        return QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .exclusiveStartKey(exclusiveStartKey)
                .limit(limit)
                .build();
    }

    private LastEvaluatedKey buildLastEvaluatedKey(Page<Recipient> recipientPage) {
        if (MapUtils.isNotEmpty(recipientPage.lastEvaluatedKey())) {
            var lastEvaluatedKey = recipientPage.lastEvaluatedKey();

            var bankAccountIdAttr = lastEvaluatedKey.get("bankAccountId");
            var recipientNameAttr = lastEvaluatedKey.get("recipientName");

            if (bankAccountIdAttr == null || recipientNameAttr == null) {
                logger.error("Missing required attributes in last evaluated key for recipient page.");
                throw new IllegalStateException("Missing required attributes in last evaluated key.");
            }

            try {
                var bankAccountId = UUID.fromString(bankAccountIdAttr.s());
                var recipientName = recipientNameAttr.s();

                logger.debug("Last evaluated key retrieved - BankAccountId: {}, RecipientName: {}", bankAccountId, recipientName);
                return new LastEvaluatedKey(bankAccountId, recipientName);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid UUID format for bankAccountId in last evaluated key.", e);
                throw new IllegalStateException("Invalid UUID format for bankAccountId in last evaluated key.", e);
            }
        }
        logger.debug("No last evaluated key found for recipient page.");
        return null;
    }
}
