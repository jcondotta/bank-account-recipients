package com.jcondotta.recipients.configuration.ssm;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.ssm.parameters")
public record ParametersConfiguration(JwtSignatureSecretConfiguration jwtSignatureSecret) {}