package com.jcondotta.recipients.factory;

import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.service.request.AddRecipientRequest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RecipientDTOTestFactory {

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

    public static RecipientDTO createRecipientDTO(AddRecipientRequest addRecipientRequest) {
        return createRecipientDTO(addRecipientRequest.bankAccountId(), addRecipientRequest.recipientName(), addRecipientRequest.recipientIban());
    }

    public static List<RecipientDTO> createRecipientsDTO(UUID bankAccountId, TestRecipient... testRecipients) {
        return Arrays.stream(testRecipients).collect(Collectors.toSet())
                .stream()
                .map(testRecipient -> createRecipientDTO(bankAccountId, testRecipient))
                .toList();
    }

    public static List<RecipientDTO> createRecipientsDTO(TestBankAccount testBankAccount, TestRecipient... testRecipients) {
        return createRecipientsDTO(testBankAccount.getBankAccountId(), testRecipients);
    }
}