package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.Collection;
import java.util.stream.Collectors;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

@Singleton
public class FetchRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(FetchRecipientService.class);

    @Inject
    private final DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    public FetchRecipientService(DynamoDbTable<Recipient> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public RecipientsDTO findRecipients(Long bankAccountId){
        logger.info("[BankAccountId={}] Fetching recipients", bankAccountId);

        var recipients = dynamoDbTable.query(keyEqualTo(k -> k.partitionValue(bankAccountId)))
                .items()
                .stream()
                .map(recipient -> new RecipientDTO(recipient))
                .collect(Collectors.toList());

        logger.info("[BankAccountId={}] {} recipient(s) found", bankAccountId, recipients.size());
        return new RecipientsDTO(recipients);
    }
}
