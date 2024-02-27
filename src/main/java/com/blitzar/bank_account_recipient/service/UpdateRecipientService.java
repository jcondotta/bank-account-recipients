package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import com.blitzar.bank_account_recipient.service.request.UpdateRecipientRequest;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

@Singleton
public class UpdateRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateRecipientService.class);

    private final RecipientRepository recipientRepository;
    private final Clock currentInstant;
    private final Validator validator;

    public UpdateRecipientService(RecipientRepository recipientRepository, Clock currentInstant, Validator validator) {
        this.recipientRepository = recipientRepository;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public void updateRecipient(@NotNull Long bankAccountId, @NotBlank String recipientId, @NotNull UpdateRecipientRequest updateRecipientRequest){
        logger.info("Attempting to update a recipient to bank account id: {}", bankAccountId);

        var recipient = recipientRepository.find(bankAccountId, recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("No recipient has been found with id: " + recipientId + " related to bank account: " + bankAccountId));

        var constraintViolations = validator.validate(updateRecipientRequest);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }

        if(!updateRecipientRequest.name().equals(recipient.getName())){
            recipient.setName(updateRecipientRequest.name());
        }

        if(!updateRecipientRequest.iban().equals(recipient.getIban())){
            recipient.setIban(updateRecipientRequest.iban());
        }

        recipientRepository.update(recipient);
    }
}
