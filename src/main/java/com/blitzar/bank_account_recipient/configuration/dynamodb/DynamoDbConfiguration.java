package com.blitzar.bank_account_recipient.configuration.dynamodb;

import io.micronaut.context.annotation.ConfigurationProperties;

import java.util.Optional;

@ConfigurationProperties("aws.dynamodb")
public record DynamoDbConfiguration(DynamoDbEndpointConfiguration dynamoDbEndpointConfiguration) {

    @ConfigurationProperties(value = "")
    public record DynamoDbEndpointConfiguration(Optional<String> endpoint) { }
}