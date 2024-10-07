package com.blitzar.bank_account_recipient.helper;

import com.blitzar.bank_account_recipient.domain.Recipient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Singleton
public class RecipientTablePurgeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipientTablePurgeService.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    public RecipientTablePurgeService(DynamoDbTable<Recipient> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    public void purgeTable() {
        logger.info("Purging all items from {} DynamoDB table", dynamoDbTable.tableName());

        try {
            var recipients = dynamoDbTable.scan()
                    .items().stream()
                    .toList();

            recipients.forEach(dynamoDbTable::deleteItem);
            logger.info("Successfully purged {} items.", recipients.size());
        }
        catch (Exception e) {
            logger.error("Error purging recipient items from DynamoDB table", e);
        }
    }
}