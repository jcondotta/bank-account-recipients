package com.jcondotta.recipients.helper;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Singleton
public class RecipientTablePurgeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientTablePurgeService.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;
    private final RedisCommands<String, RecipientsDTO> redisCommands;

    @Inject
    public RecipientTablePurgeService(DynamoDbTable<Recipient> dynamoDbTable, RedisCommands<String, RecipientsDTO> redisCommands) {
        this.dynamoDbTable = dynamoDbTable;
        this.redisCommands = redisCommands;
    }

    public void purgeTable() {
        LOGGER.info("Purging all items from {} DynamoDB table", dynamoDbTable.tableName());

        try {
            var recipients = dynamoDbTable.scan()
                    .items().stream()
                    .toList();

            recipients.forEach(dynamoDbTable::deleteItem);
            LOGGER.info("Successfully purged {} items.", recipients.size());

            redisCommands.scan().getKeys().forEach(key -> {
                System.out.println("bye key: " + key);
                redisCommands.del(key);
            });
        }
        catch (Exception e) {
            LOGGER.error("Error purging recipient items from DynamoDB table", e);
        }
    }
}