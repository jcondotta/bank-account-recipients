package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import com.blitzar.bank_account_recipient.service.request.DeleteRecipientRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.UUID;

@Singleton
public class DeleteRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteRecipientService.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;
    private final Validator validator;

    @Inject
    public DeleteRecipientService(DynamoDbTable<Recipient> dynamoDbTable, Validator validator) {
        this.dynamoDbTable = dynamoDbTable;
        this.validator = validator;
    }

    public void deleteRecipient(@NotNull DeleteRecipientRequest deleteRecipientRequest) {
        UUID bankAccountId = deleteRecipientRequest.bankAccountId();
        String recipientName = deleteRecipientRequest.recipientName();

        logger.info("[BankAccountId={}, RecipientName={}] Attempting to delete a recipient", bankAccountId, recipientName);

        var constraintViolations = validator.validate(deleteRecipientRequest);
        if (!constraintViolations.isEmpty()) {
            logger.warn("[BankAccountId={}] Validation errors for recipientName {}: {}", bankAccountId, recipientName, constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(bankAccountId.toString())
                .sortValue(recipientName)
                .build());

        if (recipient != null) {
            dynamoDbTable.deleteItem(recipient);
            logger.info("[BankAccountId={}, RecipientName={}] Recipient deleted successfully: {}", bankAccountId, recipientName, recipient);
        } else {
            logger.warn("[BankAccountId={}, RecipientName={}] Attempted to delete a non-existent recipient", bankAccountId, recipientName);
            throw new RecipientNotFoundException("recipient.notFound", bankAccountId, recipientName);
        }
    }
}