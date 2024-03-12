package com.blitzar.bank_account_recipient.factory.aws;

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
public class AWSDynamoDBFactory {

    @Value("${aws.region}")
    protected String region;

    @Nullable
    @Value("${aws.dynamodb.endpoint}")
    protected String dynamoDBEndpoint;

    @Value("${aws.dynamodb.table-name}")
    protected String tableName;

    @Singleton
    @Primary
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider awsCredentialsProvider){
        var endpointOverride = Objects.nonNull(dynamoDBEndpoint) ? URI.create(dynamoDBEndpoint) : null;

        return DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .endpointOverride(endpointOverride)
                .build();
    }

    @Singleton
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Singleton
    public DynamoDbTable<Recipient> recipientDynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient){
        DynamoDbTable<Recipient> recipientsTable = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Recipient.class));
//        recipientsTable.createTable();

        return recipientsTable;
    }
}