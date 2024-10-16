package com.blitzar.bank_account_recipient.configuration.ssm;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.ssm.parameters.jwt-signature-secret")
public record JwtSignatureSecretConfiguration(String name) {}