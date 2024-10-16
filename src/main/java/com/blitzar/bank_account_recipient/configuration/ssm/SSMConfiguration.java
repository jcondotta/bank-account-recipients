package com.blitzar.bank_account_recipient.configuration.ssm;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.ssm")
public record SSMConfiguration(EndpointConfiguration endpoint, ParametersConfiguration parameters) {

    @ConfigurationProperties(value = "")
    public record EndpointConfiguration(String endpoint) { }
}