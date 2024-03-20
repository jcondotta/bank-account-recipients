package com.blitzar.bank_account_recipient.factory.aws;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Factory
public class AWSDynamoDBFactory {

    @Singleton
    public DynamoDbClient dynamoDbClient(@Value("${aws.region}") String region){
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Singleton
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Singleton
    public DynamoDbTable<Recipient> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient, @Value("${aws.dynamodb.table-name}") String tableName){
        return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Recipient.class));
    }
}