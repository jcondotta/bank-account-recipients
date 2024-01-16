package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.StreamSupport;

@Singleton
public class FetchRecipientService {

    private static final Logger logger = LoggerFactory.getLogger(FetchRecipientService.class);
    private RecipientRepository recipientRepository;

    @Inject
    public FetchRecipientService(RecipientRepository recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    public RecipientsDTO findRecipients(Long bankAccountId){
        logger.info("Fetching recipients from bank account id: {}", bankAccountId);

        var recipients = StreamSupport.stream(recipientRepository.find(bankAccountId).spliterator(), false)
                .map(recipient -> new RecipientDTO(recipient))
                .toList();

        return new RecipientsDTO(recipients);
    }
}
