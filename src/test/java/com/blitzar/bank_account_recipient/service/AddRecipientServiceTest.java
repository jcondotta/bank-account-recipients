package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.TestValidatorBuilder;
import com.blitzar.bank_account_recipient.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddRecipientServiceTest {

    private Long bankAccountId = 998372L;
    private String recipientName = "Jefferson Condotta";
    private String recipientIBAN = "DE00 0000 0000 0000 00";

    private AddRecipientService addRecipientService;

    private Clock testClockUTC = ClockTestFactory.testClockFixedInstant;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    private static final Validator VALIDATOR = TestValidatorBuilder.getValidator();

    @BeforeEach
    public void beforeEach(){
        addRecipientService = new AddRecipientService(dynamoDbTable, testClockUTC, VALIDATOR);
    }

    @Test
    public void givenValidRequest_whenAddRecipient_thenSaveCard(){
        var addRecipientRequest = new AddRecipientRequest(recipientName, recipientIBAN);

        addRecipientService.addRecipient(bankAccountId, addRecipientRequest);
        verify(dynamoDbTable).putItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidRecipientName_whenAddRecipient_thenThrowException(String invalidRecipientName){
        var addRecipientRequest = new AddRecipientRequest(invalidRecipientName, recipientIBAN);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addRecipientService.addRecipient(bankAccountId, addRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("recipient.name.notBlank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("name")
                ));

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidRecipientIBAN_whenAddRecipient_thenThrowException(String invalidRecipientIBAN){
        var addRecipientRequest = new AddRecipientRequest(recipientName, invalidRecipientIBAN);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addRecipientService.addRecipient(bankAccountId, addRecipientRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("recipient.iban.notBlank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("iban")
                ));

        verify(dynamoDbTable, never()).putItem(any(Recipient.class));
    }
}
