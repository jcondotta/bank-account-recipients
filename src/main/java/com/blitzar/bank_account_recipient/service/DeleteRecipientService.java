package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DeleteRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteRecipientService.class);
    private RecipientRepository recipientRepository;

    public DeleteRecipientService(RecipientRepository recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    public void deleteRecipient(Long bankAccountId, String recipientId){
        logger.info("Attempting to delete a recipient from bank account id: {}", bankAccountId);

        var recipient = recipientRepository.find(bankAccountId, recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("No recipient has been found with id: " + recipientId + " related to bank account: " + bankAccountId));

        recipientRepository.delete(recipient);
    }
}
