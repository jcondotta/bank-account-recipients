package com.blitzar.bank_account_recipient.factory.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Factory
public class AWSDynamoDBFactory {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.dynamodb.endpoint}")
    private String dynamoDBEndpoint;

    @Value("${aws.dynamodb.table-name}")
    private String tableName;

    @Singleton
    AmazonDynamoDBAsync amazonDynamoDBAsync(AWSStaticCredentialsProvider credentialsProvider) {
        var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(dynamoDBEndpoint, region);

        return AmazonDynamoDBAsyncClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    @Singleton
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB){
        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Singleton
    @Primary
    public DynamoDbClient dynamoDbClient(){
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(dynamoDBEndpoint))
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