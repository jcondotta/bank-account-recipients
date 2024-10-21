package com.jcondotta.recipients.configuration.dynamodb;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.dynamodb.tables.recipients")
public record RecipientsTableConfiguration(String tableName) { }