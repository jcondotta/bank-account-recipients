package com.blitzar.bank_account_recipient.configuration.dynamodb;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("aws.dynamodb.tables")
public record TablesConfiguration(RecipientsTableConfiguration recipientsTableConfiguration) {}