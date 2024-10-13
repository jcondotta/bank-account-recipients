package com.blitzar.bank_account_recipient.validation.recipient.test;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import com.blitzar.bank_account_recipient.factory.RecipientDTOTestFactory;
import com.blitzar.bank_account_recipient.factory.RecipientTestFactory;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.validation.recipient.RecipientDTOValidator;
import com.blitzar.bank_account_recipient.validation.recipient.RecipientValidator;
import com.blitzar.bank_account_recipient.validation.recipient.RecipientsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RecipientsValidatorTest {

    private RecipientsValidator recipientsValidator;
    private RecipientValidator mockRecipientValidator;
    private RecipientDTOValidator mockRecipientDTOValidator;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @BeforeEach
    void setUp() {
        mockRecipientValidator = mock(RecipientValidator.class);
        mockRecipientDTOValidator = mock(RecipientDTOValidator.class);
        recipientsValidator = new RecipientsValidator(mockRecipientValidator, mockRecipientDTOValidator);
    }

    @Test
    void shouldThrowException_whenDTOListIsNull() {
        List<RecipientDTO> expectedRecipients = null;
        List<RecipientDTO> actualRecipients = RecipientDTOTestFactory.createRecipientsDTO(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        assertThrows(NullPointerException.class, () -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, actualRecipients));
    }

    @Test
    void shouldThrowException_whenEntityListIsNull() {
        List<Recipient> expectedRecipients = null;
        List<Recipient> actualRecipients = RecipientTestFactory.createRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);

        assertThrows(NullPointerException.class, () -> recipientsValidator.validateEntitiesAgainstEntities(expectedRecipients, actualRecipients));
    }

    @Test
    void shouldNotThrowException_whenBothListsAreEmpty() {
        List<RecipientDTO> expectedRecipients = List.of();
        List<RecipientDTO> actualRecipients = List.of();

        assertDoesNotThrow(() -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, actualRecipients));
    }

    @Test
    void shouldThrowRecipientNotFoundException_whenActualRecipientIsNull() {
        List<RecipientDTO> expectedRecipients = RecipientDTOTestFactory.createRecipientsDTO(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);
        List<RecipientDTO> actualRecipients = List.of();

        assertThrows(RecipientNotFoundException.class, () -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, actualRecipients));
    }

    @Test
    void shouldThrowRecipientNotFoundException_whenExpectedRecipientIsNotFound() {
        List<RecipientDTO> expectedRecipients = RecipientDTOTestFactory.createRecipientsDTO(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);
        List<RecipientDTO> actualRecipients = RecipientDTOTestFactory.createRecipientsDTO(TestBankAccount.BRAZIL, TestRecipient.PATRIZIO);

        assertThrows(RecipientNotFoundException.class, () -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, actualRecipients));
    }

    @Test
    void shouldValidateDTOsAgainstEntitiesSuccessfully_whenRecipientsMatch() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);
        Recipient actualRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);

        List<RecipientDTO> expectedRecipients = List.of(expectedRecipientDTO);
        List<Recipient> actualRecipients = List.of(actualRecipient);

        assertDoesNotThrow(() -> recipientsValidator.validateDTOsAgainstEntities(expectedRecipients, actualRecipients));
        verify(mockRecipientDTOValidator).validate(expectedRecipientDTO, actualRecipient);
    }

    @Test
    void shouldValidateEntitiesAgainstEntitiesSuccessfully_whenRecipientsMatch() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);
        Recipient actualRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);

        List<Recipient> expectedRecipients = List.of(expectedRecipient);
        List<Recipient> actualRecipients = List.of(actualRecipient);

        assertDoesNotThrow(() -> recipientsValidator.validateEntitiesAgainstEntities(expectedRecipients, actualRecipients));
        verify(mockRecipientValidator).validate(expectedRecipient, actualRecipient);
    }

    @Test
    void shouldNotValidate_whenRecipientsDoNotMatch() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);
        Recipient actualRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.PATRIZIO);

        List<RecipientDTO> expectedRecipients = List.of(expectedRecipientDTO);
        List<Recipient> actualRecipients = List.of(actualRecipient);

        assertThrows(RecipientNotFoundException.class, () -> recipientsValidator.validateDTOsAgainstEntities(expectedRecipients, actualRecipients));
        verify(mockRecipientDTOValidator, never()).validate(expectedRecipientDTO, actualRecipient);
    }
}
