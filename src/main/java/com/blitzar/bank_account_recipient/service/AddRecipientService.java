package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.Clock;
import java.time.LocalDateTime;

@Singleton
public class AddRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(AddRecipientService.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;
    private final Clock currentInstant;
    private final Validator validator;

    @Inject
    public AddRecipientService(DynamoDbTable<Recipient> dynamoDbTable, Clock currentInstant, Validator validator) {
        this.dynamoDbTable = dynamoDbTable;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public RecipientDTO addRecipient(@NotNull AddRecipientRequest addRecipientRequest) {
        var bankAccountId = addRecipientRequest.bankAccountId();
        var recipientName = addRecipientRequest.recipientName();
        var recipientIBAN = addRecipientRequest.recipientIban();

        logger.info("[BankAccountId={}, RecipientName={}, IBAN={}] Attempting to add a recipient",
                bankAccountId, recipientName, recipientIBAN);

        var constraintViolations = validator.validate(addRecipientRequest);
        if (!constraintViolations.isEmpty()) {
            logger.warn("[BankAccountId={}, RecipientName={}, IBAN={}] Validation errors for request. Violations: {}",
                    bankAccountId, recipientName, recipientIBAN, constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }

        var recipient = new Recipient(bankAccountId, recipientName, recipientIBAN, LocalDateTime.now(currentInstant));
        dynamoDbTable.putItem(recipient);

        logger.info("[BankAccountId={}, RecipientName={}, IBAN={}] Recipient saved to DB", bankAccountId, recipientName, recipientIBAN);
        logger.debug("Saved Recipient: {}", recipient);

        return new RecipientDTO(recipient);
    }
}