package com.jcondotta.recipients.service.dto;

import com.jcondotta.recipients.domain.Recipient;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
@Schema(name = "ExistentRecipientDTO", description = "Represents an existing recipient entity returned when the recipient already exists.")
public class ExistentRecipientDTO extends RecipientDTO{

    public ExistentRecipientDTO(Recipient recipient) {
        super(recipient);
    }
}
