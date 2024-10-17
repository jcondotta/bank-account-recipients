package com.blitzar.bank_account_recipient.factory.aws;

import com.blitzar.bank_account_recipient.configuration.dynamodb.DynamoDbConfiguration;
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
    @Requires(missing = DynamoDbConfiguration.EndpointConfiguration.class)
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
    @Requires(bean = DynamoDbConfiguration.EndpointConfiguration.class)
    public DynamoDbClient dynamoDbClientEndpointOverridden(AwsCredentials awsCredentials, Region region, DynamoDbConfiguration.EndpointConfiguration endpointConfiguration){
        LOGGER.info("Building DynamoDbClient with params: awsCredentials: {}, region: {} and endpoint: {}", awsCredentials, region, endpointConfiguration.endpoint());

        return DynamoDbClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpointConfiguration.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}