package com.jcondotta.recipients.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws")
public record AwsConfiguration(String accessKeyId, String secretKey, String region) {}