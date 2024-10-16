package com.blitzar.bank_account_recipient.factory.aws;

import com.blitzar.bank_account_recipient.configuration.dynamodb.RecipientsTableConfiguration;
import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

import java.time.LocalDateTime;
import java.util.UUID;

@Factory
public class DynamoDBTableFactory {

    private RecipientsTableConfiguration recipientsTableConfiguration;

    public DynamoDBTableFactory(RecipientsTableConfiguration recipientsTableConfiguration) {
        this.recipientsTableConfiguration = recipientsTableConfiguration;
    }

    @Singleton
    @Requires(bean = RecipientsTableConfiguration.class)
    public DynamoDbTable<Recipient> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient){
        StaticTableSchema<Recipient> staticTableSchema = StaticTableSchema.builder(Recipient.class)
                .newItemSupplier(Recipient::new)
                .addAttribute(UUID.class, attr -> attr.name("bankAccountId")
                        .getter(Recipient::getBankAccountId)
                        .setter(Recipient::setBankAccountId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(String.class, attr -> attr.name("recipientName")
                        .getter(Recipient::getRecipientName)
                        .setter(Recipient::setRecipientName)
                        .tags(StaticAttributeTags.primarySortKey()))
                .addAttribute(String.class, attr -> attr.name("recipientIban")
                        .getter(Recipient::getRecipientIban)
                        .setter(Recipient::setRecipientIban))
                .addAttribute(LocalDateTime.class, attr -> attr.name("createdAt")
                        .getter(Recipient::getCreatedAt)
                        .setter(Recipient::setCreatedAt))
                .build();


        return dynamoDbEnhancedClient.table(recipientsTableConfiguration.tableName(), staticTableSchema);
    }
}