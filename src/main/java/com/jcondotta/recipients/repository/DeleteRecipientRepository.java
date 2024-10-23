package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
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

    public void deleteRecipient(UUID bankAccountId, String recipientName) {
        var recipientKey = Key.builder()
                .partitionValue(bankAccountId.toString())
                .sortValue(recipientName)
                .build();

        try {
            dynamoDbTable.deleteItem(builder -> builder.key(recipientKey)
                    .conditionExpression(Expression.builder().expression("attribute_exists(bankAccountId) AND attribute_exists(recipientName)")
                            .build()));

            LOGGER.info("[BankAccountId={}, RecipientName={}] Recipient deleted successfully", bankAccountId, recipientName);

        }
        catch (ConditionalCheckFailedException e) {
            LOGGER.warn("[BankAccountId={}, RecipientName={}] Attempted to delete a non-existent recipient", bankAccountId, recipientName);
            throw new RecipientNotFoundException("recipient.notFound", bankAccountId, recipientName);
        }
    }
}
