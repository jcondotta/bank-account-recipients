package com.jcondotta.recipients.configuration.ssm;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.ssm.parameters.jwt-signature-secret")
public record JwtSignatureSecretConfiguration(String name) {}