package com.blitzar.bank_account_recipient.factory;

import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class RecipientDTOFactory {

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;

    public static RecipientDTO createRecipientDTO(UUID bankAccountId, String recipientName, String recipientIban, LocalDateTime createdAt) {
        return new RecipientDTO(bankAccountId, recipientName, recipientIban, createdAt);
    }

    public static RecipientDTO createRecipientDTO(UUID bankAccountId, String recipientName, String recipientIban) {
        return createRecipientDTO(bankAccountId, recipientName, recipientIban, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
    }

    public static RecipientDTO createRecipientDTO(UUID bankAccountId, TestRecipient testRecipient) {
        return createRecipientDTO(bankAccountId, testRecipient.getRecipientName(), testRecipient.getRecipientIban());
    }

    public static RecipientDTO createRecipientDTO(TestBankAccount testBankAccount, TestRecipient testRecipient) {
        return createRecipientDTO(testBankAccount.getBankAccountId(), testRecipient);
    }
}