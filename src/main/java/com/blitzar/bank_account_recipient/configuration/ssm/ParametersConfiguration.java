package com.blitzar.bank_account_recipient.configuration.ssm;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.ssm.parameters")
public record ParametersConfiguration(JwtSignatureSecretConfiguration jwtSignatureSecret) {}