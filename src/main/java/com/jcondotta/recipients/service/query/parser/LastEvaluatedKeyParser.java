package com.jcondotta.recipients.service.query.parser;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

public class LastEvaluatedKeyParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(LastEvaluatedKeyParser.class);

    private static final String BANK_ACCOUNT_ID_KEY = "bankAccountId";
    private static final String RECIPIENT_NAME_KEY = "recipientName";

    public LastEvaluatedKey parse(Page<Recipient> recipientsPage) {
        if (MapUtils.isNotEmpty(recipientsPage.lastEvaluatedKey())) {
            var lastEvaluatedKey = recipientsPage.lastEvaluatedKey();

            // Extract recipientName and bankAccountId
            String recipientName = extractRecipientName(lastEvaluatedKey);
            UUID bankAccountId = extractBankAccountId(lastEvaluatedKey);

            LOGGER.debug("Last evaluated key retrieved - BankAccountId: {}, RecipientName: {}", bankAccountId, recipientName);
            return new LastEvaluatedKey(bankAccountId, recipientName); // Return both as required by DynamoDB
        }
        LOGGER.debug("No last evaluated key found for recipient page.");
        return null;
    }

    private String extractRecipientName(Map<String, AttributeValue> lastEvaluatedKey) {
        var recipientNameAttr = lastEvaluatedKey.get(RECIPIENT_NAME_KEY);

        if (recipientNameAttr == null) {
            LOGGER.error("Missing recipientName in last evaluated key for recipient page.");
            throw new IllegalStateException("Missing recipientName in last evaluated key.");
        }

        return recipientNameAttr.s();
    }

    private UUID extractBankAccountId(Map<String, AttributeValue> lastEvaluatedKey) {
        var bankAccountIdAttr = lastEvaluatedKey.get(BANK_ACCOUNT_ID_KEY);

        if (bankAccountIdAttr == null) {
            LOGGER.error("Missing bankAccountId in last evaluated key for recipient page.");
            throw new IllegalStateException("Missing bankAccountId in last evaluated key.");
        }

        try {
            return UUID.fromString(bankAccountIdAttr.s()); // Convert String to UUID
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid UUID format for bankAccountId in last evaluated key.");
            throw new IllegalStateException("Invalid UUID format for bankAccountId in last evaluated key.", e);
        }
    }
}
