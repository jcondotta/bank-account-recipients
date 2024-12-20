package com.jcondotta.recipients.validation.recipient.test;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientNotFoundException;
import com.jcondotta.recipients.factory.RecipientDTOTestFactory;
import com.jcondotta.recipients.factory.RecipientTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.validation.recipient.RecipientsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecipientsValidatorTest {

    private RecipientsValidator recipientsValidator;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @BeforeEach
    void beforeEach() {
        recipientsValidator = new RecipientsValidator();
    }

    @Test
    void shouldThrowException_whenDTOListIsNull() {
        List<RecipientDTO> expectedRecipients = null;
        List<RecipientDTO> actualRecipients = RecipientDTOTestFactory.createRecipientsDTO(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        assertThrows(IllegalArgumentException.class, () -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, actualRecipients));
    }

    @Test
    void shouldThrowException_whenEntityListIsNull() {
        List<Recipient> expectedRecipients = null;
        List<Recipient> actualRecipients = RecipientTestFactory.createRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);

        assertThrows(IllegalArgumentException.class, () -> recipientsValidator.validateEntitiesAgainstEntities(expectedRecipients, actualRecipients));
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
    }

    @Test
    void shouldValidateEntitiesAgainstEntitiesSuccessfully_whenRecipientsMatch() {
        Recipient expectedRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);
        Recipient actualRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);

        List<Recipient> expectedRecipients = List.of(expectedRecipient);
        List<Recipient> actualRecipients = List.of(actualRecipient);

        assertDoesNotThrow(() -> recipientsValidator.validateEntitiesAgainstEntities(expectedRecipients, actualRecipients));
    }

    @Test
    void shouldNotValidate_whenRecipientsDoNotMatch() {
        RecipientDTO expectedRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);
        Recipient actualRecipient = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.PATRIZIO);

        List<RecipientDTO> expectedRecipients = List.of(expectedRecipientDTO);
        List<Recipient> actualRecipients = List.of(actualRecipient);

        assertThrows(RecipientNotFoundException.class, () -> recipientsValidator.validateDTOsAgainstEntities(expectedRecipients, actualRecipients));
    }
}
