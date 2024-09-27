package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.validation.ValidatorBuilder;
import com.blitzar.bank_account_recipient.argumentprovider.*;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.factory.ClockTestFactory;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddRecipientServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;
    private static final Validator VALIDATOR = ValidatorBuilder.getValidator();

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    private AddRecipientService addRecipientService;

    @BeforeEach
    public void setup() {
        addRecipientService = new AddRecipientService(dynamoDbTable, TEST_CLOCK_FIXED_INSTANT, VALIDATOR);
    }

    @Test
    public void shouldSaveRecipient_whenRequestIsValid() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        var recipientDTO = addRecipientService.addRecipient(addRecipientRequest);

        verify(dynamoDbTable).putItem(any(Recipient.class));

        assertAll(
                () -> assertThat(recipientDTO.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL),
                () -> assertThat(recipientDTO.recipientName()).isEqualTo(RECIPIENT_NAME_JEFFERSON),
                () -> assertThat(recipientDTO.recipientIban()).isEqualTo(RECIPIENT_IBAN_JEFFERSON),
                () -> assertThat(recipientDTO.createdAt()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
        );
    }

    @Test
    public void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        var addRecipientRequest = new AddRecipientRequest(null, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("bankAccountId");
                });

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenRecipientNameIsBlank(String invalidRecipientName) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName, RECIPIENT_IBAN_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientName.notBlank");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("recipientName");
                });

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(MaliciousInputArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenRecipientNameIsMalicious(String maliciousRecipientName) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, maliciousRecipientName, RECIPIENT_IBAN_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientName.invalid");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("recipientName");
                });

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }

    @Test
    public void shouldThrowConstraintViolationException_whenRecipientNameIsTooLong() {
        final var veryLongRecipientName = "J".repeat(51);
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, veryLongRecipientName, RECIPIENT_IBAN_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientName.tooLong");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("recipientName");
                });

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenRecipientIBANIsBlank(String invalidRecipientIBAN) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIBAN);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientIban.invalid");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("recipientIban");
                });

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(MaliciousInputArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenRecipientIBANIsMalicious(String invalidRecipientIBAN) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIBAN);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientIban.invalid");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("recipientIban");
                });

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidIBANArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenRecipientIBANIsInvalid(String invalidRecipientIBAN) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIBAN);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientIban.invalid");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("recipientIban");
                });

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }

    @Test
    void shouldThrowMultipleConstraintViolationExceptions_whenAllFieldsAreNull() {
        var addRecipientRequest = new AddRecipientRequest(null, null, null);

        var exception = assertThrows(
                ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest)
        );

        var violations = exception.getConstraintViolations();
        assertThat(violations).hasSize(3);

        Map<String, String> expectedViolations = Map.of(
                "recipient.bankAccountId.notNull", "bankAccountId",
                "recipient.recipientName.notBlank", "recipientName",
                "recipient.recipientIban.invalid", "recipientIban"
        );

        violations.forEach(violation -> {
            String message = violation.getMessage();
            String propertyPath = violation.getPropertyPath().toString();

            assertThat(expectedViolations)
                    .containsKey(message)
                    .withFailMessage("Unexpected message: %s", message);

            assertThat(propertyPath)
                    .isEqualTo(expectedViolations.get(message))
                    .withFailMessage("Property path mismatch for message: %s", message);
        });

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }
}