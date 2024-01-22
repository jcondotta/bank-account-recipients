package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.TestValidatorBuilder;
import com.blitzar.bank_account_recipient.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import com.blitzar.bank_account_recipient.service.request.UpdateRecipientRequest;
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
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateRecipientServiceTest {

    private String recipientName = "Jefferson Condotta";
    private String recipientIBAN = "DE00 0000 0000 0000 00";

    private Long bankAccountId = 998372L;
    private String recipientId = "65a91bde6d26737862f2f2ae";

    private UpdateRecipientService updateRecipientService;

    @Mock
    private RecipientRepository recipientRepositoryMock;

    private static final Validator VALIDATOR = TestValidatorBuilder.getValidator();

    @BeforeEach
    public void beforeEach(){
        updateRecipientService = new UpdateRecipientService(recipientRepositoryMock, Clock.system(ZoneOffset.UTC), VALIDATOR);
    }

    @Test
    public void givenValidRequest_whenAddRecipient_thenSaveCard(){
        Recipient recipient = mock(Recipient.class);
        when(recipientRepositoryMock.find(bankAccountId, recipientId)).thenReturn(Optional.of(recipient));

        var updateRecipientRequest = new UpdateRecipientRequest(recipientName, recipientIBAN);

        updateRecipientService.updateRecipient(bankAccountId, recipientId, updateRecipientRequest);
        verify(recipientRepositoryMock).update(any());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidRecipientName_whenUpdateRecipient_thenThrowException(String invalidRecipientName){
        Recipient recipient = mock(Recipient.class);
        when(recipientRepositoryMock.find(bankAccountId, recipientId)).thenReturn(Optional.of(recipient));

        var updateRecipientRequest = new UpdateRecipientRequest(invalidRecipientName, recipientIBAN);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> updateRecipientService.updateRecipient(bankAccountId, recipientId, updateRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("recipient.name.notBlank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("name")
                ));

        verify(recipientRepositoryMock, never()).update(any());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidRecipientIBAN_whenAddRecipient_thenThrowException(String invalidRecipientIBAN){
        Recipient recipient = mock(Recipient.class);
        when(recipientRepositoryMock.find(bankAccountId, recipientId)).thenReturn(Optional.of(recipient));

        var updateRecipientRequest = new UpdateRecipientRequest(recipientName, invalidRecipientIBAN);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> updateRecipientService.updateRecipient(bankAccountId, recipientId, updateRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("recipient.iban.notBlank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("iban")
                ));

        verify(recipientRepositoryMock, never()).update(any());
    }
}