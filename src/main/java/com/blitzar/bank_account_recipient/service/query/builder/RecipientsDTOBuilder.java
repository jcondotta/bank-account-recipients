package com.blitzar.bank_account_recipient.service.query.builder;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RecipientsDTOBuilder {

    private final LastEvaluatedKeyBuilder lastEvaluatedKeyBuilder;

    public RecipientsDTOBuilder(LastEvaluatedKeyBuilder lastEvaluatedKeyBuilder) {
        this.lastEvaluatedKeyBuilder = lastEvaluatedKeyBuilder;
    }

    public RecipientsDTO build(Page<Recipient> recipientsPage) {
        if (recipientsPage.items().isEmpty()) {
            return new RecipientsDTO(Collections.emptyList(), 0, null);
        }
        else {
            List<RecipientDTO> recipients = recipientsPage.items().stream()
                    .map(RecipientDTO::new)
                    .collect(Collectors.toList());

            var lastEvaluatedKey = lastEvaluatedKeyBuilder.build(recipientsPage);
            return new RecipientsDTO(recipients, recipients.size(), lastEvaluatedKey);
        }
    }
}
