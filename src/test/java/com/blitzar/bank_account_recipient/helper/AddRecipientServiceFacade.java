package com.blitzar.bank_account_recipient.helper;

import com.blitzar.bank_account_recipient.service.AddRecipientService;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class AddRecipientServiceFacade {

    private final AddRecipientService addRecipientService;

    @Inject
    public AddRecipientServiceFacade(AddRecipientService addRecipientService) {
        this.addRecipientService = addRecipientService;
    }

    public RecipientDTO addRecipient(UUID bankAccountId, String recipientName, String iban) {
        var addRecipientRequest = new AddRecipientRequest(bankAccountId, recipientName, iban);
        return addRecipientService.addRecipient(addRecipientRequest);
    }

    public RecipientDTO addRecipient(UUID bankAccountId, TestRecipient testRecipient) {
        return addRecipient(bankAccountId, testRecipient.getRecipientName(), testRecipient.getRecipientIban());
    }

    public RecipientDTO addRecipient(TestBankAccount testBankAccount, String recipientName, String iban) {
        return addRecipient(testBankAccount.getBankAccountId(), recipientName, iban);
    }

    public RecipientDTO addRecipient(TestBankAccount testBankAccount, TestRecipient testRecipient) {
        return addRecipient(testBankAccount.getBankAccountId(), testRecipient.getRecipientName(), testRecipient.getRecipientIban());
    }

    public List<RecipientDTO> addRecipients(UUID bankAccountId, TestRecipient... testRecipients) {
        return Arrays.stream(testRecipients).collect(Collectors.toSet())
                .stream()
                .map(testRecipient -> addRecipient(bankAccountId, testRecipient))
                .collect(Collectors.toList());
    }

    public List<RecipientDTO> addRecipients(TestBankAccount testBankAccount, TestRecipient... testRecipients) {
        return addRecipients(testBankAccount.getBankAccountId(), testRecipients);
    }
}