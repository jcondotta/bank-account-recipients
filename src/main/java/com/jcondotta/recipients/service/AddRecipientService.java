package com.jcondotta.recipients.service;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.repository.AddRecipientRepository;
import com.jcondotta.recipients.service.cache.CacheEvictionService;
import com.jcondotta.recipients.service.dto.ExistentRecipientDTO;
import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.service.request.AddRecipientRequest;
import jakarta.inject.Inject;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AddRecipientService.class);

    private final AddRecipientRepository addRecipientRepository;
    private final CacheEvictionService cacheEvictionService;
    private final Clock currentInstant;
    private final Validator validator;

    @Inject
    public AddRecipientService(AddRecipientRepository addRecipientRepository, CacheEvictionService cacheEvictionService, Clock currentInstant, Validator validator) {
        this.addRecipientRepository = addRecipientRepository;
        this.cacheEvictionService = cacheEvictionService;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public RecipientDTO addRecipient(@NotNull AddRecipientRequest addRecipientRequest) {
        LOGGER.info("[BankAccountId={}, RecipientName={}] Attempting to add a recipient",
                addRecipientRequest.bankAccountId(), addRecipientRequest.recipientName());

        var constraintViolations = validator.validate(addRecipientRequest);
        if (!constraintViolations.isEmpty()) {
            LOGGER.warn("[BankAccountId={}, RecipientName={}] Validation errors for request. Violations: {}",
                    addRecipientRequest.bankAccountId(), addRecipientRequest.recipientName(), constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }

        var addRecipientRepositoryResponse = addRecipientRepository.add(
                new Recipient(
                    addRecipientRequest.bankAccountId(),
                    addRecipientRequest.recipientName(),
                    addRecipientRequest.recipientIban(),
                    LocalDateTime.now(currentInstant)
                )
        );

        var recipient = addRecipientRepositoryResponse.recipient();
        cacheEvictionService.evictCacheEntriesByBankAccountId(recipient.getBankAccountId());

        if (addRecipientRepositoryResponse.isIdempotent()) {
            LOGGER.debug("[BankAccountId={}, RecipientName={}] Idempotent request processed, recipient already exists",
                    recipient.getBankAccountId(), recipient.getRecipientName());

            return new ExistentRecipientDTO(recipient);
        }
        else {
            LOGGER.info("[BankAccountId={}, RecipientName={}] Recipient successfully added",
                    recipient.getBankAccountId(), recipient.getRecipientName());

            return new RecipientDTO(recipient);
        }
    }
}