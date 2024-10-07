package com.blitzar.bank_account_recipient.validation.recipient;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class RecipientDTOValidator {

    public void validate(RecipientDTO expected, RecipientDTO actual) {
        assertAll(
                () -> assertThat(actual.recipientName()).isEqualTo(expected.recipientName()),
                () -> assertThat(actual.recipientIban()).isEqualTo(expected.recipientIban()),
                () -> assertThat(actual.bankAccountId()).isEqualTo(expected.bankAccountId()),
                () -> assertThat(actual.createdAt()).isEqualTo(expected.createdAt())
        );
    }

    public void validate(RecipientDTO expected, Recipient actual) {
        assertAll(
                () -> assertThat(actual.getRecipientName()).isEqualTo(expected.recipientName()),
                () -> assertThat(actual.getRecipientIban()).isEqualTo(expected.recipientIban()),
                () -> assertThat(actual.getBankAccountId()).isEqualTo(expected.bankAccountId()),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(expected.createdAt())
        );
    }
}