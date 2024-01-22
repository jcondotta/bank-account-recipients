package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;

@Singleton
public class AddRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(AddRecipientService.class);

    private final RecipientRepository recipientRepository;
    private final Clock currentInstant;
    private final Validator validator;

    public AddRecipientService(RecipientRepository recipientRepository, Clock currentInstant, Validator validator) {
        this.recipientRepository = recipientRepository;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public Recipient addRecipient(@NotNull Long bankAccountId, @NotNull AddRecipientRequest addRecipientRequest){
        logger.info("Attempting to add a recipient to bank account id: {}", bankAccountId);

        var constraintViolations = validator.validate(addRecipientRequest);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }

        var recipient = new Recipient(addRecipientRequest.name(), addRecipientRequest.iban(), bankAccountId, LocalDateTime.now(currentInstant));
        return recipientRepository.save(recipient);
    }
}
