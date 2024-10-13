package com.blitzar.bank_account_recipient.factory;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RecipientTestFactory {

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;

    public static Recipient createRecipient(UUID bankAccountId, String recipientName, String recipientIban, LocalDateTime createdAt) {
        return new Recipient(bankAccountId, recipientName, recipientIban, createdAt);
    }

    public static Recipient createRecipient(UUID bankAccountId, String recipientName, String recipientIban) {
        return createRecipient(bankAccountId, recipientName, recipientIban, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
    }

    public static Recipient createRecipient(UUID bankAccountId, TestRecipient testRecipient) {
        return createRecipient(bankAccountId, testRecipient.getRecipientName(), testRecipient.getRecipientIban());
    }

    public static Recipient createRecipient(TestBankAccount testBankAccount, TestRecipient testRecipient) {
        return createRecipient(testBankAccount.getBankAccountId(), testRecipient);
    }

    public static Recipient createRecipient(AddRecipientRequest addRecipientRequest) {
        return createRecipient(addRecipientRequest.bankAccountId(), addRecipientRequest.recipientName(), addRecipientRequest.recipientIban());
    }

    public static List<Recipient> createRecipients(UUID bankAccountId, TestRecipient... testRecipients) {
        return Arrays.stream(testRecipients).collect(Collectors.toSet())
                .stream()
                .map(testRecipient -> createRecipient(bankAccountId, testRecipient))
                .toList();
    }

    public static List<Recipient> createRecipients(TestBankAccount testBankAccount, TestRecipient... testRecipients) {
        return createRecipients(testBankAccount.getBankAccountId(), testRecipients);
    }
}