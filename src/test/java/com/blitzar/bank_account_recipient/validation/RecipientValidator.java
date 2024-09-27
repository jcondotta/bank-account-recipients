package com.blitzar.bank_account_recipient.validation;

import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class RecipientValidator {

    private record RecipientKey(UUID bankAccountId, String name) {}

    public void validateRecipients(List<RecipientDTO> expectedRecipients, List<RecipientDTO> actualRecipients) {
        Map<RecipientKey, RecipientDTO> actualRecipientsMap = actualRecipients.stream()
                .collect(Collectors.toMap(
                        recipientDTO -> new RecipientKey(recipientDTO.bankAccountId(), recipientDTO.recipientName()),
                        recipientDTO -> recipientDTO
                ));

        expectedRecipients.forEach(expectedRecipient -> {
            var recipientKey = new RecipientKey(expectedRecipient.bankAccountId(), expectedRecipient.recipientName());

            var actualRecipient = actualRecipientsMap.getOrDefault(recipientKey, null);

            if (actualRecipient == null) {
                throw new RecipientNotFoundException("recipient.notFound", expectedRecipient.bankAccountId(), expectedRecipient.recipientName());
            }

            validateRecipientFields(expectedRecipient, actualRecipient);
        });
    }

    private void validateRecipientFields(RecipientDTO expectedRecipient, RecipientDTO actualRecipient) {
        assertAll(
                () -> assertThat(actualRecipient.recipientName()).isEqualTo(expectedRecipient.recipientName()),
                () -> assertThat(actualRecipient.recipientIban()).isEqualTo(expectedRecipient.recipientIban()),
                () -> assertThat(actualRecipient.bankAccountId()).isEqualTo(expectedRecipient.bankAccountId()),
                () -> assertThat(actualRecipient.createdAt()).isEqualTo(expectedRecipient.createdAt())
        );
    }
}