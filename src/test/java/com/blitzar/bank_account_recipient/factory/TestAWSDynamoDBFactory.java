package com.blitzar.bank_account_recipient.factory;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.Objects;

@Factory
public class TestAWSDynamoDBFactory {

    @Singleton
    @Primary
    public DynamoDbClient dynamoDbClient(@Value("${aws.region}") String region, @Value("${aws.dynamodb.endpoint}") String dynamoDBEndpoint){
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(dynamoDBEndpoint))
                .build();
    }

    @Singleton
    @Primary
    public DynamoDbTable<Recipient> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient, @Value("${aws.dynamodb.table-name}") String tableName){
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