package com.jcondotta.recipients.service;

import com.jcondotta.recipients.repository.DeleteRecipientRepository;
import com.jcondotta.recipients.service.request.DeleteRecipientRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Singleton
public class DeleteRecipientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRecipientService.class);

    private final DeleteRecipientRepository recipientRepository;
    private final Validator validator;

    @Inject
    public DeleteRecipientService(DeleteRecipientRepository recipientRepository, Validator validator) {
        this.recipientRepository = recipientRepository;
        this.validator = validator;
    }

    public void deleteRecipient(@NotNull DeleteRecipientRequest deleteRecipientRequest) {
        UUID bankAccountId = deleteRecipientRequest.bankAccountId();
        String recipientName = deleteRecipientRequest.recipientName();

        LOGGER.info("[BankAccountId={}, RecipientName={}] Attempting to delete a recipient", bankAccountId, recipientName);

        var constraintViolations = validator.validate(deleteRecipientRequest);
        if (!constraintViolations.isEmpty()) {
            LOGGER.warn("[BankAccountId={}] Validation errors for recipientName {}: {}", bankAccountId, recipientName, constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }

        recipientRepository.deleteRecipient(bankAccountId, recipientName);
    }
}