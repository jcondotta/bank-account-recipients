package com.blitzar.bank_account_recipient.listener;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Singleton
public class DynamoDBTableRecipientsCreatedEventListener implements BeanCreatedEventListener<DynamoDbTable<Recipient>> {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBTableRecipientsCreatedEventListener.class);

    @Override
    public DynamoDbTable<Recipient> onCreated(@NonNull BeanCreatedEvent<DynamoDbTable<Recipient>> event) {
        var dynamoDBTable = event.getBean();

        try {
            dynamoDBTable.describeTable();
            logger.info("DynamoDB table for type {} exists.", dynamoDBTable.tableSchema().itemType());
        }
        catch (ResourceNotFoundException e) {
            logger.warn("DynamoDB table for type {} not found. Creating the table.", dynamoDBTable.tableSchema().itemType());
            dynamoDBTable.createTable();
        }
        catch (Exception e) {
            logger.error("An unexpected error occurred while checking the DynamoDB table: {}", e.getMessage(), e);
        }

        return dynamoDBTable;
    }
}