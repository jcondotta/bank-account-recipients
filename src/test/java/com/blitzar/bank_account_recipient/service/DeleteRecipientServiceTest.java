package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.argumentprovider.BlankAndNonPrintableCharactersArgumentProvider;
import com.blitzar.bank_account_recipient.argumentprovider.malicious.MaliciousInputArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.request.DeleteRecipientRequest;
import com.blitzar.bank_account_recipient.validation.ValidatorBuilder;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipientServiceTest {

    private DeleteRecipientService deleteRecipientService;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private static final Validator VALIDATOR = ValidatorBuilder.getValidator();

    @BeforeEach
    public void setup() {
        deleteRecipientService = new DeleteRecipientService(dynamoDbTable, VALIDATOR);
    }

    @Test
    public void shouldDeleteRecipient_whenRecipientExists() {
        Recipient recipientMock = mock(Recipient.class);
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(recipientMock);

        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        deleteRecipientService.deleteRecipient(deleteRecipientRequest);

        verify(dynamoDbTable).deleteItem(recipientMock);
    }

    @Test
    public void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        var deleteRecipientRequest = new DeleteRecipientRequest(null, RECIPIENT_NAME_JEFFERSON);

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
        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName);

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
        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName);

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

        var deleteRecipientRequest = new DeleteRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        assertThatThrownBy(() -> deleteRecipientService.deleteRecipient(deleteRecipientRequest))
                .isInstanceOf(RecipientNotFoundException.class)
                .hasMessage("recipient.notFound");

        verify(dynamoDbTable, never()).deleteItem(any(Recipient.class));
    }
}