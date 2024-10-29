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
import com.jcondotta.recipients.repository.AddRecipientRepository;
import com.jcondotta.recipients.repository.AddRecipientRepositoryResponse;
import com.jcondotta.recipients.service.cache.CacheEvictionService;
import com.jcondotta.recipients.service.dto.ExistentRecipientDTO;
import com.jcondotta.recipients.service.dto.RecipientDTO;
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
    private AddRecipientRepository recipientRepository;

    @Mock
    private AddRecipientRepositoryResponse repositoryResponse;

    @Mock
    private CacheEvictionService cacheEvictionService;

    private AddRecipientService addRecipientService;

    @BeforeEach
    void beforeEach() {
        addRecipientService = new AddRecipientService(recipientRepository, cacheEvictionService, TEST_CLOCK_FIXED_INSTANT, VALIDATOR);
    }

    @ParameterizedTest
    @ArgumentsSource(EdgeCaseIbanArgumentsProvider.class)
    void shouldSaveRecipient_whenRequestIsValid(String validIban) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, validIban);

        var recipientMock = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, validIban);
        when(repositoryResponse.recipient()).thenReturn(recipientMock);
        when(repositoryResponse.isIdempotent()).thenReturn(false);

        when(recipientRepository.add(any(Recipient.class))).thenReturn(repositoryResponse);

        var recipientDTO = addRecipientService.addRecipient(addRecipientRequest);

        verify(recipientRepository).add(any(Recipient.class));
        verify(cacheEvictionService).evictCacheEntriesByBankAccountId(addRecipientRequest.bankAccountId());
        verifyNoMoreInteractions(recipientRepository, cacheEvictionService);

        assertThat(recipientDTO).isExactlyInstanceOf(RecipientDTO.class);
        assertAll(
                () -> assertThat(recipientDTO.getBankAccountId()).isEqualTo(addRecipientRequest.bankAccountId()),
                () -> assertThat(recipientDTO.getRecipientName()).isEqualTo(addRecipientRequest.recipientName()),
                () -> assertThat(recipientDTO.getRecipientIban()).isEqualTo(addRecipientRequest.recipientIban()),
                () -> assertThat(recipientDTO.getCreatedAt()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
        );
    }

    @Test
    void shouldReturnRecipientWithoutModification_whenRequestIsIdempotent() {
        var recipientMock = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        when(repositoryResponse.recipient()).thenReturn(recipientMock);
        when(repositoryResponse.isIdempotent()).thenReturn(false);

        when(recipientRepository.add(any(Recipient.class))).thenReturn(repositoryResponse);

        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        var recipientDTO = addRecipientService.addRecipient(addRecipientRequest);

        assertThat(recipientDTO).isExactlyInstanceOf(RecipientDTO.class);
        assertAll(
                () -> assertThat(recipientDTO.getBankAccountId()).isEqualTo(addRecipientRequest.bankAccountId()),
                () -> assertThat(recipientDTO.getRecipientName()).isEqualTo(addRecipientRequest.recipientName()),
                () -> assertThat(recipientDTO.getRecipientIban()).isEqualTo(addRecipientRequest.recipientIban()),
                () -> assertThat(recipientDTO.getCreatedAt()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
        );

        when(repositoryResponse.isIdempotent()).thenReturn(true);

        var existentRecipientDTO = addRecipientService.addRecipient(addRecipientRequest);
        assertThat(existentRecipientDTO).isExactlyInstanceOf(ExistentRecipientDTO.class);
        assertAll(
                () -> assertThat(existentRecipientDTO.getBankAccountId()).isEqualTo(addRecipientRequest.bankAccountId()),
                () -> assertThat(existentRecipientDTO.getRecipientName()).isEqualTo(addRecipientRequest.recipientName()),
                () -> assertThat(existentRecipientDTO.getRecipientIban()).isEqualTo(addRecipientRequest.recipientIban()),
                () -> assertThat(existentRecipientDTO.getCreatedAt()).isEqualTo(LocalDateTime.now(TEST_CLOCK_FIXED_INSTANT))
        );

        verify(cacheEvictionService, times(2)).evictCacheEntriesByBankAccountId(addRecipientRequest.bankAccountId());
    }

    @Test
    void shouldThrowRecipientAlreadyExistsException_whenSameRecipientButDifferentIbanIsAdded() {
        when(recipientRepository.add(any(Recipient.class))).thenThrow(RecipientAlreadyExistsException.class);

        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        assertThrows(RecipientAlreadyExistsException.class, () -> addRecipientService.addRecipient(addRecipientRequest));

        verify(recipientRepository).add(any(Recipient.class));
        verifyNoMoreInteractions(recipientRepository, cacheEvictionService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        var addRecipientRequest = new AddRecipientRequest(null, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientRepository, cacheEvictionService);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenRecipientNameIsBlank(String invalidRecipientName) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName, RECIPIENT_IBAN_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientRepository, cacheEvictionService);
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenRecipientNameIsMalicious(String invalidRecipientName) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName, RECIPIENT_IBAN_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientRepository, cacheEvictionService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenRecipientNameIsTooLong() {
        final var veryLongRecipientName = "J".repeat(51);
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, veryLongRecipientName, RECIPIENT_IBAN_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientRepository, cacheEvictionService);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    @ArgumentsSource(InvalidIbanArgumentsProvider.class)
    void shouldThrowConstraintViolationException_whenRecipientIbanIsInvalid(String invalidRecipientIban) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIban);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(recipientRepository, cacheEvictionService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenAllFieldsAreNull() {
        var addRecipientRequest = new AddRecipientRequest(null, null, null);

        var exception = assertThrows(ConstraintViolationException.class, () -> addRecipientService.addRecipient(addRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(3);
    }
}