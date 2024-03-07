package com.blitzar.bank_account_recipient.factory;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Factory
public class TestAWSDynamoDBFactory {

    @Value("${aws.dynamodb.table-name}")
    private String tableName;

    @Singleton
    @Primary
    public DynamoDbTable<Recipient> recipientDynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient){
        DynamoDbTable<Recipient> recipientsTable = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Recipient.class));
        try {
            recipientsTable.describeTable();
        }
        catch (Exception e){
            recipientsTable.createTable();
        }

        return recipientsTable;
    }
}