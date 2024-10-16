package com.blitzar.bank_account_recipient.configuration.dynamodb;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.dynamodb.tables.recipients")
public record RecipientsTableConfiguration(String tableName) { }