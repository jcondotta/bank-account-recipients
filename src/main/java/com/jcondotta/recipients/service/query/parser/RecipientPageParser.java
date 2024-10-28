package com.jcondotta.recipients.service.query.parser;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RecipientPageParser {

    private final LastEvaluatedKeyParser lastEvaluatedKeyParser;

    public RecipientPageParser(LastEvaluatedKeyParser lastEvaluatedKeyParser) {
        this.lastEvaluatedKeyParser = lastEvaluatedKeyParser;
    }

    public RecipientsDTO parse(Page<Recipient> recipientsPage) {
        if (Objects.nonNull(recipientsPage) && Objects.nonNull(recipientsPage.items())) {
            List<RecipientDTO> recipients = recipientsPage.items()
                    .stream()
                    .map(RecipientDTO::new)
                    .toList();

            var lastEvaluatedKey = lastEvaluatedKeyParser.parse(recipientsPage);
            return new RecipientsDTO(recipients, recipients.size(), lastEvaluatedKey);
        }
        else {
            return new RecipientsDTO(Collections.emptyList(), 0, null);
        }
    }
}
