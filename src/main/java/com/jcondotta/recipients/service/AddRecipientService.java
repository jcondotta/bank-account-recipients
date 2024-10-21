package com.jcondotta.recipients.service;

import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.service.dto.ExistentRecipientDTO;
import com.jcondotta.recipients.service.request.AddRecipientRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Clock;
import java.time.LocalDateTime;

@Singleton
public class AddRecipientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddRecipientService.class);

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
        var recipientIban = addRecipientRequest.recipientIban();

        LOGGER.info("[BankAccountId={}, RecipientName={}, IBAN={}] Attempting to add a recipient",
                bankAccountId, recipientName, recipientIban);

        var constraintViolations = validator.validate(addRecipientRequest);
        if (!constraintViolations.isEmpty()) {
            LOGGER.warn("[BankAccountId={}, RecipientName={}, IBAN={}] Validation errors for request. Violations: {}",
                    bankAccountId, recipientName, recipientIban, constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }

        try {
            var recipient = new Recipient(bankAccountId, recipientName, recipientIban, LocalDateTime.now(currentInstant));

            PutItemEnhancedRequest<Recipient> putItemEnhancedRequest = PutItemEnhancedRequest.builder(Recipient.class).item(recipient)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(bankAccountId) AND attribute_not_exists(recipientName)")
                            .build())
                    .build();

            dynamoDbTable.putItem(putItemEnhancedRequest);

            LOGGER.info("[BankAccountId={}, RecipientName={}, IBAN={}] Recipient saved to DB", bankAccountId, recipientName, recipientIban);
            LOGGER.debug("Saved Recipient: {}", recipient);

            return new RecipientDTO(recipient);
        }
        catch (ConditionalCheckFailedException e) {
            LOGGER.info("[BankAccountId={}, RecipientName={}, IBAN={}] Recipient found in DB, returning existing recipient",
                    bankAccountId, recipientName, recipientIban);

            var existingRecipient = dynamoDbTable.getItem(Key.builder()
                    .partitionValue(bankAccountId.toString())
                    .sortValue(recipientName)
                    .build());

            return new ExistentRecipientDTO(existingRecipient);
        }
    }
}