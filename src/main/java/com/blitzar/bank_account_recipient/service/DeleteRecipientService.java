package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Objects;
import java.util.UUID;

@Singleton
public class DeleteRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteRecipientService.class);

    @Inject
    private final DynamoDbTable<Recipient> dynamoDbTable;

    public DeleteRecipientService(DynamoDbTable<Recipient> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void deleteRecipient(UUID bankAccountId, String recipientName){
        logger.info("[BankAccountId={}, RecipientName={}] Attempting to delete a recipient", bankAccountId, recipientName);

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(bankAccountId.toString())
                .sortValue(recipientName)
                .build());

        if(Objects.nonNull(recipient)){
            dynamoDbTable.deleteItem(recipient);
            logger.info("[BankAccountId={}, RecipientName={}] Recipient deleted", bankAccountId, recipientName);
        }
        else{
            throw new RecipientNotFoundException("recipient.notFound", bankAccountId, recipientName);
        }
    }
}
