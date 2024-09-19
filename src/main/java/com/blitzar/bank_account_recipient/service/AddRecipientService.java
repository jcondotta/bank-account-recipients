package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Clock;
import java.time.LocalDateTime;

@Singleton
public class AddRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(AddRecipientService.class);

    private final DynamoDbTable<Recipient> dynamoDbTable;
    private final Clock currentInstant;
    private final Validator validator;

    public AddRecipientService(DynamoDbTable<Recipient> dynamoDbTable, Clock currentInstant, Validator validator) {
        this.dynamoDbTable = dynamoDbTable;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public Recipient addRecipient(@NotNull Long bankAccountId, @NotNull AddRecipientRequest addRecipientRequest){
        logger.info("[BankAccountId={}] Attempting to add a recipient", bankAccountId);

        var constraintViolations = validator.validate(addRecipientRequest);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }

        var recipient = new Recipient(bankAccountId, addRecipientRequest.name(), addRecipientRequest.iban(), LocalDateTime.now(currentInstant));
        dynamoDbTable.putItem(recipient);

        logger.info("[BankAccountId={}] Recipient saved to DB", bankAccountId);
        logger.debug(recipient.toString());

        return recipient;
    }
}
