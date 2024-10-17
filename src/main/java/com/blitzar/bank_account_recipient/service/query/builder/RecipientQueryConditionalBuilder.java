package com.blitzar.bank_account_recipient.service.query.builder;

import com.blitzar.bank_account_recipient.service.request.QueryParams;
import com.blitzar.bank_account_recipient.service.request.QueryRecipientsRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

public class RecipientQueryConditionalBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientQueryConditionalBuilder.class);

    public QueryConditional build(QueryRecipientsRequest queryRecipientsRequest) {
        var bankAccountId = queryRecipientsRequest.bankAccountId().toString();
        var recipientKeyBuilder = Key.builder().partitionValue(bankAccountId);

        var recipientNamePrefix = getRecipientNamePrefix(queryRecipientsRequest);

        if (StringUtils.isBlank(recipientNamePrefix)) {
            LOGGER.info("[BankAccountId={}] Using primary key only", bankAccountId);
            return QueryConditional.keyEqualTo(recipientKeyBuilder.build());
        }

        recipientKeyBuilder.sortValue(recipientNamePrefix);

        LOGGER.info("[BankAccountId={}] Using primary and sort key: {}", bankAccountId, recipientNamePrefix);
        return QueryConditional.sortBeginsWith(recipientKeyBuilder.build());
    }

    private String getRecipientNamePrefix(QueryRecipientsRequest queryRecipientsRequest){
        return queryRecipientsRequest.queryParams()
                .flatMap(QueryParams::recipientName)
                .orElse(null);
    }
}
