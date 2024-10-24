package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientAlreadyExistsException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;

@Singleton
public class AddRecipientRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddRecipientRepository.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    public AddRecipientRepository(DynamoDbTable<Recipient> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public AddRecipientRepositoryResponse add(Recipient recipient) {
        try {
            PutItemEnhancedRequest<Recipient> putItemEnhancedRequest = PutItemEnhancedRequest
                    .builder(Recipient.class)
                    .item(recipient)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(bankAccountId) AND attribute_not_exists(recipientName)")
                            .build())
                    .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                    .build();

            dynamoDbTable.putItem(putItemEnhancedRequest);

            LOGGER.info("[BankAccountId={}, RecipientName={}, IBAN={}] Recipient saved to DB",
                    recipient.getBankAccountId(), recipient.getRecipientName(), recipient.getRecipientIban());

            return AddRecipientRepositoryResponse.builder(recipient).build();
        }
        catch (ConditionalCheckFailedException e) {
            var recipientKey = Key.builder()
                    .partitionValue(recipient.getBankAccountId().toString())
                    .sortValue(recipient.getRecipientName())
                    .build();

            var existentRecipient = dynamoDbTable.getItem(recipientKey);
            if(!existentRecipient.getRecipientIban().equals(recipient.getRecipientIban())){
                LOGGER.warn("[BankAccountId={}, RecipientName={}, IBAN={}] Conflict: Recipient exists but with a different IBAN",
                        recipient.getBankAccountId(), recipient.getRecipientName(), recipient.getRecipientIban());

                throw new RecipientAlreadyExistsException("recipient.alreadyExists", recipient.getBankAccountId(), recipient.getRecipientName());
            }

            return AddRecipientRepositoryResponse.builder(recipient)
                    .isIdempotent(true)
                    .build();
        }
    }
}
