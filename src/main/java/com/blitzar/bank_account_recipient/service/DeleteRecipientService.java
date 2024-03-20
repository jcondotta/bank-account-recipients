package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Objects;

@Singleton
public class DeleteRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteRecipientService.class);

    @Inject
    private final DynamoDbTable<Recipient> dynamoDbTable;

    public DeleteRecipientService(DynamoDbTable<Recipient> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void deleteRecipient(Long bankAccountId, String recipientName){
        logger.info("[BankAccountId={}] Attempting to delete a recipient with name: {}", bankAccountId, recipientName);

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(bankAccountId)
                .sortValue(recipientName)
                .build());

        if(Objects.nonNull(recipient)){
            dynamoDbTable.deleteItem(recipient);
            logger.info("[BankAccountId={}] Recipient deleted", bankAccountId);
        }
        else{
            throw new ResourceNotFoundException("[BankAccountId=" + bankAccountId + "] No recipient has been found with name: " + recipientName);
        }
    }
}
