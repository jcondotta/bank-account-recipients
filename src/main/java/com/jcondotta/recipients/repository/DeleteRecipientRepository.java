package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.UUID;

@Singleton
public class DeleteRecipientRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRecipientRepository.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    public DeleteRecipientRepository(DynamoDbTable<Recipient> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void deleteRecipient(@NotNull UUID bankAccountId, @NotNull String recipientName) {
        var recipientKey = Key.builder()
                .partitionValue(bankAccountId.toString())
                .sortValue(recipientName)
                .build();

        var deleteItemRequest = DeleteItemEnhancedRequest.builder()
                .key(recipientKey)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(bankAccountId) AND attribute_exists(recipientName)")
                        .build())
                .build();

        try {
            dynamoDbTable.deleteItem(deleteItemRequest);
            LOGGER.info("[BankAccountId={}, RecipientName={}] Recipient deleted successfully", bankAccountId, recipientName);
        }
        catch (ConditionalCheckFailedException e) {
            LOGGER.warn("[BankAccountId={}, RecipientName={}] Attempted to delete a non-existent recipient", bankAccountId, recipientName);
            throw new RecipientNotFoundException("recipient.notFound", bankAccountId, recipientName);
        }
    }
}
