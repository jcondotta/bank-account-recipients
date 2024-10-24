package com.jcondotta.recipients.service;

import com.jcondotta.recipients.argument_provider.validation.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.iban.EdgeCaseIbanArgumentsProvider;
import com.jcondotta.recipients.argument_provider.validation.iban.InvalidIbanArgumentsProvider;
import com.jcondotta.recipients.argument_provider.validation.security.ThreatInputArgumentProvider;
import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientAlreadyExistsException;
import com.jcondotta.recipients.factory.ClockTestFactory;
import com.jcondotta.recipients.factory.RecipientTestFactory;
import com.jcondotta.recipients.factory.ValidatorTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.repository.DeleteRecipientRepository;
import com.jcondotta.recipients.service.request.AddRecipientRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddRecipientServiceTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    private static final Clock TEST_CLOCK_FIXED_INSTANT = ClockTestFactory.testClockFixedInstant;
    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @Mock
    private DeleteRecipientRepository recipientRepository;

    private AddRecipientService addRecipientService;

    @BeforeEach
    void beforeEach() {
        addRecipientService = new AddRecipientService(null, TEST_CLOCK_FIXED_INSTANT, VALIDATOR);
    }

//    @ParameterizedTest
//    @ArgumentsSource(EdgeCaseIbanArgumentsProvider.class)
//    void shouldSaveRecipient_whenRequestIsValid(String validIban) {
//        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, validIban);
//        var recipientDTO = addRecipientService.addRecipient(addRecipientRequest);
//
////        verify(recipientRepository).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//
//        assertAll(
//                () -> assertThat(recipientDTO.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL),
//                () -> assertThat(recipientDTO.getRecipientName()).isEqualTo(RECIPIENT_NAME_JEFFERSON),
//                () -> assertThat(recipientDTO.getRecipientIban()).isEqualTo(validIban),
//                () -> assertThat(recipientDTO.getCreatedAt()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
//        );
//    }
//
//    @Test
//    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
//        var addRecipientRequest = new AddRecipientRequest(null, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("recipient.bankAccountId.notNull");
//                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
//                });
//
//        verify(recipientRepository, never()).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
//    void shouldThrowConstraintViolationException_whenRecipientNameIsBlank(String invalidRecipientName) {
//        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName, RECIPIENT_IBAN_JEFFERSON);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientName.notBlank");
//                    assertThat(violation.getPropertyPath()).hasToString("recipientName");
//                });
//
//        verify(recipientRepository, never()).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(ThreatInputArgumentProvider.class)
//    void shouldThrowConstraintViolationException_whenRecipientNameIsMalicious(String invalidRecipientName) {
//        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName, RECIPIENT_IBAN_JEFFERSON);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientName.invalid");
//                    assertThat(violation.getPropertyPath()).hasToString("recipientName");
//                });
//
//        verify(recipientRepository, never()).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//    }
//
//    @Test
//    void shouldThrowConstraintViolationException_whenRecipientNameIsTooLong() {
//        final var veryLongRecipientName = "J".repeat(51);
//        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, veryLongRecipientName, RECIPIENT_IBAN_JEFFERSON);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientName.tooLong");
//                    assertThat(violation.getPropertyPath()).hasToString("recipientName");
//                });
//
//        verify(recipientRepository, never()).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
//    void shouldThrowConstraintViolationException_whenRecipientIbanIsBlank(String invalidRecipientIban) {
//        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIban);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientIban.invalid");
//                    assertThat(violation.getPropertyPath()).hasToString("recipientIban");
//                });
//
//        verify(recipientRepository, never()).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(ThreatInputArgumentProvider.class)
//    void shouldThrowConstraintViolationException_whenRecipientIbanIsMalicious(String invalidRecipientIban) {
//        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIban);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientIban.invalid");
//                    assertThat(violation.getPropertyPath()).hasToString("recipientIban");
//                });
//
//        verify(recipientRepository, never()).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(InvalidIbanArgumentsProvider.class)
//    void shouldThrowConstraintViolationException_whenRecipientIbanIsInvalid(String invalidRecipientIban) {
//        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIban);
//
//        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
//        assertThat(exception.getConstraintViolations())
//                .hasSize(1)
//                .first()
//                .satisfies(violation -> {
//                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientIban.invalid");
//                    assertThat(violation.getPropertyPath()).hasToString("recipientIban");
//                });
//
//        verify(recipientRepository, never()).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//    }
//
//    @Test
//    void shouldThrowConstraintViolationException_whenAllFieldsAreNull() {
//        var addRecipientRequest = new AddRecipientRequest(null, null, null);
//
//        var exception = assertThrows(
//                ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest)
//        );
//
//        var violations = exception.getConstraintViolations();
//        assertThat(violations).hasSize(3);
//
//        Map<String, String> expectedViolations = Map.of(
//                "recipient.bankAccountId.notNull", "bankAccountId",
//                "recipient.recipientName.notBlank", "recipientName",
//                "recipient.recipientIban.invalid", "recipientIban"
//        );
//
//        violations.forEach(violation -> {
//            String message = violation.getMessage();
//            String propertyPath = violation.getPropertyPath().toString();
//
//            assertThat(expectedViolations)
//                    .containsKey(message)
//                    .withFailMessage("Unexpected message: %s", message);
//
//            assertThat(propertyPath)
//                    .isEqualTo(expectedViolations.get(message))
//                    .withFailMessage("Property path mismatch for message: %s", message);
//        });
//
//        verify(recipientRepository, never()).saveRecipient(any(Recipient.class));
//        verifyNoMoreInteractions(recipientRepository);
//    }
//
//    @Test
//    void shouldReturnExistentRecipient_whenAttemptToSaveExistentRecipient() {
//        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
//        var jeffersonRecipientDTO = addRecipientService.addRecipient(addRecipientRequest);
//
//        doThrow(RecipientAlreadyExistsException.class).when(recipientRepository).saveRecipient(any(Recipient.class));
//
//        var existentRecipient = RecipientTestFactory.createRecipient(addRecipientRequest);
//        when(recipientRepository.findRecipient(any(), any())).thenReturn(Optional.of(existentRecipient));
//
//        addRecipientService.addRecipient(addRecipientRequest);
//
//        verify(recipientRepository, times(2)).saveRecipient(any(Recipient.class));
//        verify(recipientRepository).findRecipient(any(), any());
//        verifyNoMoreInteractions(recipientRepository);
//
//        assertAll(
//                () -> assertThat(existentRecipient.getBankAccountId()).isEqualTo(jeffersonRecipientDTO.getBankAccountId()),
//                () -> assertThat(existentRecipient.getRecipientName()).isEqualTo(jeffersonRecipientDTO.getRecipientName()),
//                () -> assertThat(existentRecipient.getRecipientIban()).isEqualTo(jeffersonRecipientDTO.getRecipientIban()),
//                () -> assertThat(existentRecipient.getCreatedAt()).isEqualTo(jeffersonRecipientDTO.getCreatedAt())
//        );
//    }
}