package com.blitzar.bank_account_recipient.validation.recipient;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class RecipientsValidator {

    private record RecipientKey(UUID bankAccountId, String name) {}

    private final RecipientValidator recipientValidator;
    private final RecipientDTOValidator recipientDTOValidator;

    public RecipientsValidator() {
        this(new RecipientValidator(), new RecipientDTOValidator());
    }

    public RecipientsValidator(RecipientValidator recipientValidator, RecipientDTOValidator recipientDTOValidator) {
        this.recipientValidator = recipientValidator;
        this.recipientDTOValidator = recipientDTOValidator;
    }


    public void validateDTOsAgainstDTOs(List<RecipientDTO> expectedRecipients, List<RecipientDTO> actualRecipients) {
        validateRecipientsByKey(expectedRecipients, actualRecipients, recipientDTOValidator::validate);
    }

    public void validateDTOsAgainstEntities(List<RecipientDTO> expectedRecipients, List<Recipient> actualRecipients) {
        validateRecipientsByKey(expectedRecipients, actualRecipients, recipientDTOValidator::validate);
    }

    public void validateEntitiesAgainstEntities(List<Recipient> expectedRecipients, List<Recipient> actualRecipients) {
        validateRecipientsByKey(expectedRecipients, actualRecipients, recipientValidator::validate);
    }

    public void validateEntitiesAgainstDTOs(List<Recipient> expectedRecipients, List<RecipientDTO> actualRecipients) {
        validateRecipientsByKey(expectedRecipients, actualRecipients, recipientValidator::validate);
    }

    private <T, U> void validateRecipientsByKey(List<T> expectedRecipients, List<U> actualRecipients, BiConsumer<T, U> validationFunction) {
        Map<RecipientKey, U> actualRecipientsMap = actualRecipients.stream()
                .collect(Collectors.toMap(
                        recipient -> new RecipientKey(getBankAccountId(recipient), getRecipientName(recipient)),
                        recipient -> recipient
                ));

        expectedRecipients.forEach(expectedRecipient -> {
            var recipientKey = new RecipientKey(getBankAccountId(expectedRecipient), getRecipientName(expectedRecipient));
            var actualRecipient = actualRecipientsMap.get(recipientKey);

            if (actualRecipient == null) {
                // Recipient is not found, so we throw the RecipientNotFoundException
                throw new RecipientNotFoundException("recipient.notFound", getBankAccountId(expectedRecipient), getRecipientName(expectedRecipient));
            }

            // Recipient is found, now we proceed with field validation
            validationFunction.accept(expectedRecipient, actualRecipient);
        });
    }

    private UUID getBankAccountId(Object recipient) {
        if (recipient instanceof RecipientDTO dto) {
            return dto.getBankAccountId();
        } else if (recipient instanceof Recipient entity) {
            return entity.getBankAccountId();
        }
        throw new IllegalArgumentException("Invalid recipient type");
    }

    private String getRecipientName(Object recipient) {
        if (recipient instanceof RecipientDTO dto) {
            return dto.getRecipientName();
        } else if (recipient instanceof Recipient entity) {
            return entity.getRecipientName();
        }
        throw new IllegalArgumentException("Invalid recipient type");
    }
}


