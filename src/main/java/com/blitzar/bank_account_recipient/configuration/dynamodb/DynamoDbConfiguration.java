package com.blitzar.bank_account_recipient.configuration.dynamodb;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.dynamodb")
public record DynamoDbConfiguration(EndpointConfiguration endpointConfiguration) {

    @ConfigurationProperties(value = "")
    public record EndpointConfiguration(String endpoint) { }
}