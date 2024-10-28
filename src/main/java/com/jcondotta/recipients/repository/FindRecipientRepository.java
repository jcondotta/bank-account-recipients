package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FindRecipientRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindRecipientRepository.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    public FindRecipientRepository(DynamoDbTable<Recipient> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public Optional<Recipient> findRecipient(UUID bankAccountId, String recipientName) {
        LOGGER.info("[BankAccountId={}, RecipientName={}] Start retrieving recipient from DynamoDB", bankAccountId, recipientName);

        var recipientKey = Key.builder()
                .partitionValue(bankAccountId.toString())
                .sortValue(recipientName)
                .build();

        var recipient = dynamoDbTable.getItem(recipientKey);
        if(Objects.nonNull(recipient)){
            LOGGER.info("[BankAccountId={}, RecipientName={}] Successfully retrieved recipient", bankAccountId, recipientName);
        }
        else{
            LOGGER.warn("[BankAccountId={}, RecipientName={}] Recipient not found", bankAccountId, recipientName);
        }

        return Optional.ofNullable(recipient);
    }
}
