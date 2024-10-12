package com.blitzar.bank_account_recipient.service.query.parser;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.request.LastEvaluatedKey;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.UUID;

public class LastEvaluatedKeyParser {

    private static final Logger logger = LoggerFactory.getLogger(LastEvaluatedKeyParser.class);

    private static final String BANK_ACCOUNT_ID_KEY = "bankAccountId";
    private static final String RECIPIENT_NAME_KEY = "recipientName";

    public LastEvaluatedKey parse(Page<Recipient> recipientsPage) {
        if (MapUtils.isNotEmpty(recipientsPage.lastEvaluatedKey())) {
            var lastEvaluatedKey = recipientsPage.lastEvaluatedKey();

            var bankAccountIdAttr = lastEvaluatedKey.get(BANK_ACCOUNT_ID_KEY);
            var recipientNameAttr = lastEvaluatedKey.get(RECIPIENT_NAME_KEY);

            if (bankAccountIdAttr == null || recipientNameAttr == null) {
                logger.error("Missing required attributes in last evaluated key for recipient page.");
                throw new IllegalStateException("Missing required attributes in last evaluated key.");
            }

            try {
                var bankAccountId = UUID.fromString(bankAccountIdAttr.s());
                var recipientName = recipientNameAttr.s();

                logger.debug("Last evaluated key retrieved - BankAccountId: {}, RecipientName: {}", bankAccountId, recipientName);
                return new LastEvaluatedKey(bankAccountId, recipientName);
            }
            catch (IllegalArgumentException e) {
                logger.error("Invalid UUID format for bankAccountId in last evaluated key.");
                throw new IllegalStateException("Invalid UUID format for bankAccountId in last evaluated key.");
            }
        }
        logger.debug("No last evaluated key found for recipient page.");
        return null;
    }
}
