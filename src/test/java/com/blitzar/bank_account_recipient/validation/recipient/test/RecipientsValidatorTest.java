package com.blitzar.bank_account_recipient.validation.recipient.test;

import com.blitzar.bank_account_recipient.factory.ClockTestFactory;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.validation.recipient.RecipientsValidator;

import java.time.Clock;
import java.util.UUID;

public class RecipientsValidatorTest {

    private final RecipientsValidator recipientsValidator = new RecipientsValidator();

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    private static final String RECIPIENT_NAME_PATRIZIO = TestRecipient.PATRIZIO.getRecipientName();
    private static final String RECIPIENT_IBAN_PATRIZIO = TestRecipient.PATRIZIO.getRecipientIban();

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;


    // TODO finish the tests for this class
//    @Test
//    public void shouldValidateListOfRecipientDTOs_whenExpectedAndActualDataMatch() {
//        List<RecipientDTO> expectedList = List.of(
//                RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//        List<RecipientDTO> actualList = List.of(
//                RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//
//        recipientsValidator.validateDTOsAgainstDTOs(expectedList, actualList);
//    }
//
//    @Test
//    public void shouldThrowRecipientNotFoundException_whenRecipientNotFoundInActualList() {
//        List<RecipientDTO> expectedList = List.of(
//                RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//        List<RecipientDTO> actualList = List.of(); // Empty list means no recipients found
//
//        assertThatThrownBy(() -> recipientsValidator.validateDTOsAgainstDTOs(expectedList, actualList))
//                .isInstanceOf(RecipientNotFoundException.class);  // Expecting RecipientNotFoundException
//    }
//
//    @Test
//    public void shouldValidateListOfRecipients_whenExpectedAndActualDataMatch() {
//        List<Recipient> expectedList = List.of(
//                RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//        List<Recipient> actualList = List.of(
//                RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//
//        recipientsValidator.validateEntitiesAgainstEntities(expectedList, actualList);
//    }
//
//    @Test
//    public void shouldThrowAssertionError_whenListOfRecipientsIbanDoesNotMatch() {
//        List<Recipient> expectedList = List.of(
//                RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//        List<Recipient> actualList = List.of(
//                RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_PATRIZIO, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//
//        assertThatThrownBy(() -> recipientsValidator.validateEntitiesAgainstEntities(expectedList, actualList))
//                .isInstanceOf(AssertionError.class);
//    }
//
//    @Test
//    public void shouldValidateListOfRecipientsAgainstRecipientDTOs_whenExpectedAndActualDataMatch() {
//        List<Recipient> expectedList = List.of(
//                RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//        List<RecipientDTO> actualList = List.of(
//                RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//
//        recipientsValidator.validateEntitiesAgainstDTOs(expectedList, actualList);
//    }
//
//    @Test
//    public void shouldThrowAssertionError_whenListOfRecipientDTOsNameDoesNotMatch() {
//        List<RecipientDTO> expectedList = List.of(
//                RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//        List<RecipientDTO> actualList = List.of(
//                RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_PATRIZIO, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//
//        assertThatThrownBy(() -> recipientsValidator.validateDTOsAgainstDTOs(expectedList, actualList))
//                .isInstanceOf(AssertionError.class);  // Expecting an AssertionError for name mismatch
//    }
//
//    @Test
//    public void shouldValidateListOfRecipientDTOsAgainstRecipients_whenExpectedAndActualDataMatch() {
//        List<RecipientDTO> expectedList = List.of(
//                RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//        List<Recipient> actualList = List.of(
//                RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//
//        recipientsValidator.validateDTOsAgainstEntities(expectedList, actualList);
//    }
//
//    @Test
//    public void shouldThrowAssertionError_whenListOfRecipientDTOsAndRecipientsIbanDoesNotMatch() {
//        List<RecipientDTO> expectedList = List.of(
//                RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//        List<Recipient> actualList = List.of(
//                RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_PATRIZIO, LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//
//        assertThatThrownBy(() -> recipientsValidator.validateDTOsAgainstEntities(expectedList, actualList))
//                .isInstanceOf(AssertionError.class);
//    }
}

