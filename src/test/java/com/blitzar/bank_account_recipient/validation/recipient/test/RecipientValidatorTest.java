package com.blitzar.bank_account_recipient.validation.recipient.test;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.factory.ClockTestFactory;
import com.blitzar.bank_account_recipient.factory.RecipientDTOTestFactory;
import com.blitzar.bank_account_recipient.factory.RecipientTestFactory;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.validation.recipient.RecipientValidator;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RecipientValidatorTest {

    private final RecipientValidator recipientValidator = new RecipientValidator();

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    private static final String RECIPIENT_NAME_PATRIZIO = TestRecipient.PATRIZIO.getRecipientName();
    private static final String RECIPIENT_IBAN_PATRIZIO = TestRecipient.PATRIZIO.getRecipientIban();

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;

    @Test
    public void shouldValidateRecipients_whenExpectedAndActualDataMatch() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        Recipient actualRecipient = RecipientTestFactory.createRecipient(expectedRecipient.getBankAccountId(), expectedRecipient.getRecipientName(), expectedRecipient.getRecipientIban(), expectedRecipient.getCreatedAt());

        recipientValidator.validate(expectedRecipient, actualRecipient);
    }

    @Test
    public void shouldThrowAssertionError_whenExpectedRecipientNameDoesNotMatchActualRecipientName() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        Recipient actualRecipient = RecipientTestFactory.createRecipient(expectedRecipient.getBankAccountId(), RECIPIENT_NAME_PATRIZIO, expectedRecipient.getRecipientIban(), expectedRecipient.getCreatedAt());

        assertThatThrownBy(() -> recipientValidator.validate(expectedRecipient, actualRecipient))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void shouldThrowAssertionError_whenExpectedRecipientIbanDoesNotMatchActualRecipientIban() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        Recipient actualRecipient = RecipientTestFactory.createRecipient(expectedRecipient.getBankAccountId(), expectedRecipient.getRecipientName(), RECIPIENT_IBAN_PATRIZIO, expectedRecipient.getCreatedAt());

        assertThatThrownBy(() -> recipientValidator.validate(expectedRecipient, actualRecipient))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void shouldThrowAssertionError_whenExpectedRecipientCreatedAtDoesNotMatchActualRecipientCreatedAt() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        Recipient actualRecipient = RecipientTestFactory.createRecipient(expectedRecipient.getBankAccountId(), expectedRecipient.getRecipientName(), expectedRecipient.getRecipientIban(), expectedRecipient.getCreatedAt().plusDays(1));

        assertThatThrownBy(() -> recipientValidator.validate(expectedRecipient, actualRecipient))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void shouldValidateRecipientAgainstRecipientDTO_whenExpectedAndActualDataMatch() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        RecipientDTO actualRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(expectedRecipient.getBankAccountId(), expectedRecipient.getRecipientName(), expectedRecipient.getRecipientIban(), expectedRecipient.getCreatedAt());

        recipientValidator.validate(expectedRecipient, actualRecipientDTO);
    }

    @Test
    public void shouldThrowAssertionError_whenExpectedRecipientNameDoesNotMatchActualRecipientDTOName() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        RecipientDTO actualRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(expectedRecipient.getBankAccountId(), RECIPIENT_NAME_PATRIZIO, expectedRecipient.getRecipientIban(), expectedRecipient.getCreatedAt());

        assertThatThrownBy(() -> recipientValidator.validate(expectedRecipient, actualRecipientDTO))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void shouldThrowAssertionError_whenExpectedRecipientIbanDoesNotMatchActualRecipientDTOIban() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        RecipientDTO actualRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(expectedRecipient.getBankAccountId(), expectedRecipient.getRecipientName(), RECIPIENT_IBAN_PATRIZIO, expectedRecipient.getCreatedAt());

        assertThatThrownBy(() -> recipientValidator.validate(expectedRecipient, actualRecipientDTO))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void shouldThrowAssertionError_whenExpectedRecipientCreatedAtDoesNotMatchActualRecipientDTOCreatedAt() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        RecipientDTO actualRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(expectedRecipient.getBankAccountId(), expectedRecipient.getRecipientName(), expectedRecipient.getRecipientIban(), expectedRecipient.getCreatedAt().plusDays(1));

        assertThatThrownBy(() -> recipientValidator.validate(expectedRecipient, actualRecipientDTO))
                .isInstanceOf(AssertionError.class);
    }
}

