package com.blitzar.bank_account_recipient.service.query.parser;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import com.blitzar.bank_account_recipient.service.query.parser.LastEvaluatedKeyParser;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RecipientPageParser {

    private final LastEvaluatedKeyParser lastEvaluatedKeyParser;

    public RecipientPageParser(LastEvaluatedKeyParser lastEvaluatedKeyParser) {
        this.lastEvaluatedKeyParser = lastEvaluatedKeyParser;
    }

    public RecipientsDTO parse(Page<Recipient> recipientsPage) {
        if (Objects.nonNull(recipientsPage) && Objects.nonNull(recipientsPage.items())) {
            List<RecipientDTO> recipients = recipientsPage.items().stream()
                    .map(RecipientDTO::new)
                    .collect(Collectors.toList());

            var lastEvaluatedKey = lastEvaluatedKeyParser.parse(recipientsPage);
            return new RecipientsDTO(recipients, recipients.size(), lastEvaluatedKey);
        }
        else {
            return new RecipientsDTO(Collections.emptyList(), 0, null);
        }
    }
}
