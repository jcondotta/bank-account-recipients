package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.TestValidatorBuilder;
import com.blitzar.bank_account_recipient.argumentprovider.BlankAndNonPrintableCharactersArgumentProvider;
import com.blitzar.bank_account_recipient.argumentprovider.MaliciousInputArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import com.blitzar.bank_account_recipient.service.request.DeleteRecipientRequest;
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
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipientServiceTest {

    private DeleteRecipientService deleteRecipientService;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    private static final UUID BANK_ACCOUNT_ID = UUID.fromString("01920bfe-017f-712e-b6f8-21d1a2853169");
    private static final String RECIPIENT_NAME = "Jefferson Condotta";

    private static final Validator VALIDATOR = TestValidatorBuilder.getValidator();

    @BeforeEach
    public void setup() {
        deleteRecipientService = new DeleteRecipientService(dynamoDbTable, VALIDATOR);
    }

    @Test
    public void shouldDeleteRecipient_whenRecipientExists() {
        Recipient recipientMock = mock(Recipient.class);
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(recipientMock);

        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID, RECIPIENT_NAME);
        deleteRecipientService.deleteRecipient(deleteRecipientRequest);

        verify(dynamoDbTable).deleteItem(recipientMock);
    }

    @Test
    public void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        var deleteRecipientRequest = new DeleteRecipientRequest(null, RECIPIENT_NAME);

        var exception = assertThrows(ConstraintViolationException.class, () -> deleteRecipientService.deleteRecipient(deleteRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("bankAccountId");
                });

        verify(dynamoDbTable, never()).deleteItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenRecipientNameIsBlank(String invalidRecipientName) {
        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID, invalidRecipientName);

        var exception = assertThrows(ConstraintViolationException.class, () -> deleteRecipientService.deleteRecipient(deleteRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientName.notBlank");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("recipientName");
                });

        verify(dynamoDbTable, never()).deleteItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(MaliciousInputArgumentProvider.class)
    public void shouldThrowConstraintViolationException_whenRecipientNameIsMalicious(String invalidRecipientName) {
        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID, invalidRecipientName);

        var exception = assertThrows(ConstraintViolationException.class, () -> deleteRecipientService.deleteRecipient(deleteRecipientRequest));
        assertThat(exception.getConstraintViolations())
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("recipient.recipientName.invalid");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("recipientName");
                });

        verify(dynamoDbTable, never()).deleteItem(any(Recipient.class));
    }

    @Test
    public void shouldThrowRecipientNotFoundException_whenRecipientDoesNotExist() {
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(null);

        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID, RECIPIENT_NAME);
        assertThatThrownBy(() -> deleteRecipientService.deleteRecipient(deleteRecipientRequest))
                .isInstanceOf(RecipientNotFoundException.class)
                .hasMessage("recipient.notFound");

        verify(dynamoDbTable, never()).deleteItem(any(Recipient.class));
    }
}