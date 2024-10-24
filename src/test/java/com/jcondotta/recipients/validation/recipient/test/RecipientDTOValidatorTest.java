package com.jcondotta.recipients.validation.recipient.test;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.factory.ClockTestFactory;
import com.jcondotta.recipients.factory.RecipientDTOTestFactory;
import com.jcondotta.recipients.factory.RecipientTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.validation.recipient.RecipientDTOValidator;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientDTOValidatorTest {

    private final RecipientDTOValidator recipientDTOValidator = new RecipientDTOValidator();

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    private static final String RECIPIENT_NAME_PATRIZIO = TestRecipient.PATRIZIO.getRecipientName();
    private static final String RECIPIENT_IBAN_PATRIZIO = TestRecipient.PATRIZIO.getRecipientIban();

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;

    @Test
    void shouldValidateRecipientDTOs_whenExpectedAndActualDataMatch() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        RecipientDTO actualRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(expectedRecipientDTO.getBankAccountId(), expectedRecipientDTO.getRecipientName(), expectedRecipientDTO.getRecipientIban(), expectedRecipientDTO.getCreatedAt());

        recipientDTOValidator.validate(expectedRecipientDTO, actualRecipientDTO);
    }

    @Test
    void shouldThrowAssertionError_whenExpectedRecipientDTONameDoesNotMatchActualRecipientDTOName() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        RecipientDTO actualRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(expectedRecipientDTO.getBankAccountId(), RECIPIENT_NAME_PATRIZIO, expectedRecipientDTO.getRecipientIban(), expectedRecipientDTO.getCreatedAt());

        assertThatThrownBy(() -> recipientDTOValidator.validate(expectedRecipientDTO, actualRecipientDTO))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void shouldThrowAssertionError_whenExpectedRecipientDTOIbanDoesNotMatchActualRecipientDTOIban() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        RecipientDTO actualRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(expectedRecipientDTO.getBankAccountId(), expectedRecipientDTO.getRecipientName(), RECIPIENT_IBAN_PATRIZIO, expectedRecipientDTO.getCreatedAt());

        assertThatThrownBy(() -> recipientDTOValidator.validate(expectedRecipientDTO, actualRecipientDTO))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void shouldThrowAssertionError_whenExpectedRecipientDTOCreatedAtDoesNotMatchActualRecipientDTOCreatedAt() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        RecipientDTO actualRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(expectedRecipientDTO.getBankAccountId(), expectedRecipientDTO.getRecipientName(), expectedRecipientDTO.getRecipientIban(), expectedRecipientDTO.getCreatedAt().plusDays(1));

        assertThatThrownBy(() -> recipientDTOValidator.validate(expectedRecipientDTO, actualRecipientDTO))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void shouldValidateRecipientDTOAgainstRecipient_whenExpectedAndActualDataMatch() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        Recipient actualRecipient = RecipientTestFactory.createRecipient(expectedRecipientDTO.getBankAccountId(), expectedRecipientDTO.getRecipientName(), expectedRecipientDTO.getRecipientIban(), expectedRecipientDTO.getCreatedAt());

        recipientDTOValidator.validate(expectedRecipientDTO, actualRecipient);
    }

    @Test
    void shouldThrowAssertionError_whenExpectedRecipientDTONameDoesNotMatchActualRecipientName() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        Recipient actualRecipient = RecipientTestFactory.createRecipient(expectedRecipientDTO.getBankAccountId(), RECIPIENT_NAME_PATRIZIO, expectedRecipientDTO.getRecipientIban(), expectedRecipientDTO.getCreatedAt());

        assertThatThrownBy(() -> recipientDTOValidator.validate(expectedRecipientDTO, actualRecipient))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void shouldThrowAssertionError_whenExpectedRecipientDTOIbanDoesNotMatchActualRecipientIban() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        Recipient actualRecipient = RecipientTestFactory.createRecipient(expectedRecipientDTO.getBankAccountId(), expectedRecipientDTO.getRecipientName(), RECIPIENT_IBAN_PATRIZIO, expectedRecipientDTO.getCreatedAt());

        assertThatThrownBy(() -> recipientDTOValidator.validate(expectedRecipientDTO, actualRecipient))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void shouldThrowAssertionError_whenExpectedRecipientDTOCreatedAtDoesNotMatchActualRecipientCreatedAt() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT));
        Recipient actualRecipient = RecipientTestFactory.createRecipient(expectedRecipientDTO.getBankAccountId(), expectedRecipientDTO.getRecipientName(), expectedRecipientDTO.getRecipientIban(), expectedRecipientDTO.getCreatedAt().plusDays(1));

        assertThatThrownBy(() -> recipientDTOValidator.validate(expectedRecipientDTO, actualRecipient))
                .isInstanceOf(AssertionError.class);
    }
}
