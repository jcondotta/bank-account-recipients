package com.jcondotta.recipients.service;

import com.jcondotta.recipients.argument_provider.validation.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.security.ThreatInputArgumentProvider;
import com.jcondotta.recipients.factory.ValidatorTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.repository.DeleteRecipientRepository;
import com.jcondotta.recipients.service.cache.CacheEvictionService;
import com.jcondotta.recipients.service.request.DeleteRecipientRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipientServiceTest {

    private DeleteRecipientService deleteRecipientService;

    @Mock
    private DeleteRecipientRepository deleteRecipientRepository;

    @Mock
    private CacheEvictionService cacheEvictionService;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @BeforeEach
    void beforeEach() {
        deleteRecipientService = new DeleteRecipientService(deleteRecipientRepository, cacheEvictionService, VALIDATOR);
    }

    @Test
    void shouldDeleteRecipient_whenRecipientExists() {
        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        deleteRecipientService.deleteRecipient(deleteRecipientRequest);

        verify(deleteRecipientRepository).delete(any(UUID.class), anyString());
        verify(cacheEvictionService).evictCacheEntriesByBankAccountId(deleteRecipientRequest.bankAccountId());
        verifyNoMoreInteractions(deleteRecipientRepository);
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        var deleteRecipientRequest = new DeleteRecipientRequest(null, RECIPIENT_NAME_JEFFERSON);

        var exception = assertThrows(ConstraintViolationException.class, () -> deleteRecipientService.deleteRecipient(deleteRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(deleteRecipientRepository, cacheEvictionService);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldThrowConstraintViolationException_whenRecipientNameIsInvalid(String invalidRecipientName) {
        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName);

        var exception = assertThrows(ConstraintViolationException.class, () -> deleteRecipientService.deleteRecipient(deleteRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(deleteRecipientRepository, cacheEvictionService);
    }

    @Test
    void shouldThrowConstraintViolationException_whenRecipientNameExceeds50Characters() {
        final var veryLongRecipientName = "J".repeat(51);
        final var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, veryLongRecipientName);

        var exception = assertThrows(ConstraintViolationException.class, () -> deleteRecipientService.deleteRecipient(deleteRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        verifyNoInteractions(deleteRecipientRepository, cacheEvictionService);
    }
}