package com.blitzar.bank_account_recipient.factory.aws;

import com.blitzar.bank_account_recipient.configuration.dynamodb.DynamoDbConfiguration;
import com.blitzar.bank_account_recipient.configuration.dynamodb.DynamoDbConfiguration.DynamoDbEndpointConfiguration;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Factory
public class DynamoDBClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBClientFactory.class);

    @Singleton
    @Replaces(DynamoDbClient.class)
    @Requires(property = "aws.dynamodb.endpoint", pattern = "^$")
    public DynamoDbClient dynamoDbClient(Region region){
        var environmentVariableCredentialsProvider = EnvironmentVariableCredentialsProvider.create();
        var awsCredentials = environmentVariableCredentialsProvider.resolveCredentials();

        LOGGER.info("Building DynamoDbClient with params: awsCredentials: {} and region: {}", awsCredentials, region);

        return DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(environmentVariableCredentialsProvider)
                .build();
    }

    @Singleton
    @Replaces(DynamoDbClient.class)
    @Requires(property = "aws.dynamodb.endpoint", pattern = "(.|\\s)*\\S(.|\\s)*")
    public DynamoDbClient dynamoDbClientEndpointOverridden(AwsCredentials awsCredentials, Region region, DynamoDbEndpointConfiguration dynamoDbEndpointConfiguration){
        LOGGER.info("Building DynamoDbClient with params: awsCredentials: {}, region: {} and endpoint: {}", awsCredentials, region, dynamoDbEndpointConfiguration.endpoint());

        var dynamoDbEndpoint = dynamoDbEndpointConfiguration.endpoint()
                .orElseThrow(() -> new IllegalStateException("Dynamo DB endpoint configuration is missing or invalid."));

        return DynamoDbClient.builder()
                .region(region)
                .endpointOverride(URI.create(dynamoDbEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}