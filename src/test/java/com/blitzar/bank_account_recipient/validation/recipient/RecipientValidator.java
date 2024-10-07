package com.blitzar.bank_account_recipient.validation.recipient;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class RecipientValidator {

    public void validate(Recipient expected, Recipient actual) {
        assertAll(
                () -> assertThat(actual.getRecipientName()).isEqualTo(expected.getRecipientName()),
                () -> assertThat(actual.getRecipientIban()).isEqualTo(expected.getRecipientIban()),
                () -> assertThat(actual.getBankAccountId()).isEqualTo(expected.getBankAccountId()),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt())
        );
    }

    public void validate(Recipient expected, RecipientDTO actual) {
        assertAll(
                () -> assertThat(actual.recipientName()).isEqualTo(expected.getRecipientName()),
                () -> assertThat(actual.recipientIban()).isEqualTo(expected.getRecipientIban()),
                () -> assertThat(actual.bankAccountId()).isEqualTo(expected.getBankAccountId()),
                () -> assertThat(actual.createdAt()).isEqualTo(expected.getCreatedAt())
        );
    }
}