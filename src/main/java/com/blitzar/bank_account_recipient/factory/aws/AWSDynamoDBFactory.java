package com.blitzar.bank_account_recipient.factory.aws;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.LocalDateTime;

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
        StaticTableSchema<Recipient> staticTableSchema = StaticTableSchema.builder(Recipient.class)
                .newItemSupplier(Recipient::new)
                .addAttribute(Long.class, attr -> attr.name("bankAccountId")
                        .getter(Recipient::getBankAccountId)
                        .setter(Recipient::setBankAccountId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(String.class, attr -> attr.name("name")
                        .getter(Recipient::getName)
                        .setter(Recipient::setName)
                        .tags(StaticAttributeTags.primarySortKey()))
                .addAttribute(String.class, attr -> attr.name("iban")
                        .getter(Recipient::getIban)
                        .setter(Recipient::setIban))
                .addAttribute(LocalDateTime.class, attr -> attr.name("createdAt")
                        .getter(Recipient::getCreatedAt)
                        .setter(Recipient::setCreatedAt))
                .build();


        return dynamoDbEnhancedClient.table(tableName, staticTableSchema);
    }
}