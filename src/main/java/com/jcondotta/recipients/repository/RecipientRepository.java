package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientAlreadyExistsException;
import com.jcondotta.recipients.exception.RecipientNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class RecipientRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientRepository.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    public RecipientRepository(DynamoDbTable<Recipient> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void saveRecipient(Recipient recipient) {
        try {
            PutItemEnhancedRequest<Recipient> putItemEnhancedRequest = PutItemEnhancedRequest.builder(Recipient.class)
                    .item(recipient)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(bankAccountId) AND attribute_not_exists(recipientName)")
                            .build())
                    .build();

            dynamoDbTable.putItem(putItemEnhancedRequest);

            LOGGER.info("[BankAccountId={}, RecipientName={}, IBAN={}] Recipient saved to DB",
                    recipient.getBankAccountId(), recipient.getRecipientName(), recipient.getRecipientIban());
        }
        catch (ConditionalCheckFailedException e) {
            LOGGER.warn("[BankAccountId={}, RecipientName={}] Unable to save recipient: A recipient with the same bank account ID" +
                            " and recipient name already exists in the database.", recipient.getBankAccountId(), recipient.getRecipientName());

            throw new RecipientAlreadyExistsException("recipient.alreadyExists", recipient.getBankAccountId(), recipient.getRecipientName());
        }
    }

    public Optional<Recipient> findRecipient(UUID bankAccountId, String recipientName) {
        LOGGER.info("[BankAccountId={}, RecipientName={}] Retrieving Recipient", bankAccountId, recipientName);

        var recipientKey = Key.builder()
                .partitionValue(bankAccountId.toString())
                .sortValue(recipientName)
                .build();

        var recipient = dynamoDbTable.getItem(recipientKey);

        return Optional.ofNullable(recipient);
    }

    public void deleteRecipient(UUID bankAccountId, String recipientName) {
        findRecipient(bankAccountId, recipientName)
            .ifPresentOrElse(recipient -> {
                dynamoDbTable.deleteItem(recipient);
                LOGGER.info("[BankAccountId={}, RecipientName={}] Recipient deleted successfully", bankAccountId, recipientName);
            },
            () -> {
                LOGGER.warn("[BankAccountId={}, RecipientName={}] Attempted to delete a non-existent recipient", bankAccountId, recipientName);
                throw new RecipientNotFoundException("recipient.notFound", bankAccountId, recipientName);
            }
        );
    }
}
