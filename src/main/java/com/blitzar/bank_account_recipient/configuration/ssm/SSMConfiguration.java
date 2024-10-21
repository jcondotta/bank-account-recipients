package com.blitzar.bank_account_recipient.configuration.ssm;

import io.micronaut.context.annotation.ConfigurationProperties;

import java.util.Optional;

@ConfigurationProperties("aws.ssm")
public record SSMConfiguration(SSMEndpointConfiguration endpoint, ParametersConfiguration parameters) {

    @ConfigurationProperties(value = "")
    public record SSMEndpointConfiguration(Optional<String> endpoint) { }
}