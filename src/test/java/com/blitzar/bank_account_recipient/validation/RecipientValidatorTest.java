package com.blitzar.bank_account_recipient.validation;

import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import com.blitzar.bank_account_recipient.factory.ClockTestFactory;
import com.blitzar.bank_account_recipient.factory.RecipientDTOFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.blitzar.bank_account_recipient.helper.TestRecipient.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RecipientValidatorTest {

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;

    private RecipientValidator recipientValidator;
    private RecipientDTOFactory factory;

    @BeforeEach
    public void beforeEach() {
        recipientValidator = new RecipientValidator();
        factory = new RecipientDTOFactory();
    }

    @Test
    public void shouldThrowNullPointerException_whenExpectedRecipientsIsNull() {
        var recipientDTO = RecipientDTOFactory.createRecipientDTO(TestBankAccount.BRAZIL, JEFFERSON);
        var actualRecipients = List.of(recipientDTO);

        assertThrows(NullPointerException.class, () -> recipientValidator.validateRecipients(null, actualRecipients));
    }

    @Test
    public void shouldThrowNullPointerException_whenActualRecipientsIsNull() {
        var recipientDTO = RecipientDTOFactory.createRecipientDTO(TestBankAccount.BRAZIL, JEFFERSON);
        var expectedRecipients = List.of(recipientDTO);

        assertThrows(NullPointerException.class, () -> recipientValidator.validateRecipients(expectedRecipients, null));
    }

    @Test
    public void shouldNotThrowException_whenExpectedRecipientsIsEmpty() {
        var recipientDTO = RecipientDTOFactory.createRecipientDTO(TestBankAccount.BRAZIL, JEFFERSON);
        var actualRecipients = List.of(recipientDTO);

        assertDoesNotThrow(() -> recipientValidator.validateRecipients(Collections.emptyList(), actualRecipients));
    }

    @Test
    public void shouldThrowRecipientNotFoundException_whenActualRecipientsIsEmpty() {
        var recipientDTO = RecipientDTOFactory.createRecipientDTO(TestBankAccount.BRAZIL, JEFFERSON);
        var expectedRecipients = List.of(recipientDTO);

        assertThrows(RecipientNotFoundException.class, () -> recipientValidator.validateRecipients(expectedRecipients, Collections.emptyList()));
    }

    @Test
    public void shouldNotThrowException_whenRecipientsMatch() {
        var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();
        var expectedRecipient = RecipientDTOFactory.createRecipientDTO(brazilBankAccountId, JEFFERSON);
        var actualRecipient = RecipientDTOFactory.createRecipientDTO(brazilBankAccountId, JEFFERSON);

        assertDoesNotThrow(() -> recipientValidator.validateRecipients(
                List.of(expectedRecipient),
                List.of(actualRecipient)
        ));
    }

    @Test
    public void shouldThrowRecipientNotFoundException_whenRecipientDoesNotMatch() {
        var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();
        var expectedRecipient = RecipientDTOFactory.createRecipientDTO(brazilBankAccountId, JEFFERSON);
        var actualRecipient = RecipientDTOFactory.createRecipientDTO(brazilBankAccountId, INDALECIO);

        assertThrows(RecipientNotFoundException.class, () -> recipientValidator.validateRecipients(
                List.of(expectedRecipient),
                List.of(actualRecipient)
        ));
    }

    @Test
    public void shouldThrowAssertionError_whenRecipientIbanMismatchOccurs() {
        var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();
        var expectedRecipient = RecipientDTOFactory.createRecipientDTO(brazilBankAccountId, JEFFERSON.getRecipientName(), JEFFERSON.getRecipientIban());
        var actualRecipient = RecipientDTOFactory.createRecipientDTO(brazilBankAccountId, JEFFERSON.getRecipientName(), INDALECIO.getRecipientIban());

        assertThrows(AssertionError.class, () -> recipientValidator.validateRecipients(
                List.of(expectedRecipient),
                List.of(actualRecipient)
        ));
    }

    @Test
    public void shouldThrowAssertionError_whenRecipientCreatedAtMismatchOccurs() {
        var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();
        var expectedRecipient = RecipientDTOFactory.createRecipientDTO(brazilBankAccountId, JEFFERSON.getRecipientName(), JEFFERSON.getRecipientIban());
        var actualRecipient = RecipientDTOFactory.createRecipientDTO(brazilBankAccountId, JEFFERSON.getRecipientName(), INDALECIO.getRecipientIban(), LocalDateTime.now());

        assertThrows(AssertionError.class, () -> recipientValidator.validateRecipients(
                List.of(expectedRecipient),
                List.of(actualRecipient)
        ));
    }
}
