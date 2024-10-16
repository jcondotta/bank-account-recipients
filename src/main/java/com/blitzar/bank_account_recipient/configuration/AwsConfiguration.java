package com.blitzar.bank_account_recipient.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws")
public record AwsConfiguration(String accessKeyId, String secretKey, String region) {}