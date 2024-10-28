package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientAlreadyExistsException;
import com.jcondotta.recipients.factory.RecipientTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.dto.ExistentRecipientDTO;
import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.service.request.AddRecipientRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddRecipientRepositoryTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    private static final String RECIPIENT_IBAN_PATRIZIO = TestRecipient.PATRIZIO.getRecipientIban();

    @InjectMocks
    private AddRecipientRepository addRecipientRepository;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Test
    void shouldSaveRecipient_whenRecipientIsValid() {
        var recipientMock = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        var addRecipientRepositoryResponse = addRecipientRepository.add(recipientMock);

        assertThat(addRecipientRepositoryResponse.recipient()).isEqualTo(recipientMock);
        assertThat(addRecipientRepositoryResponse.isIdempotent()).isFalse();

        verify(dynamoDbTable).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());
        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldNotDuplicateRecipient_whenSameRequestIsSentTwice() {
        var recipientMock = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        addRecipientRepository.add(recipientMock);

        doThrow(ConditionalCheckFailedException.class)
                .when(dynamoDbTable).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(recipientMock);

        var addRecipientRepositoryResponse = addRecipientRepository.add(recipientMock);

        assertThat(addRecipientRepositoryResponse.recipient()).isEqualTo(recipientMock);
        assertThat(addRecipientRepositoryResponse.isIdempotent()).isTrue();

        verify(dynamoDbTable, times(2)).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());
        verify(dynamoDbTable).getItem(any(Key.class));

        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldThrowRecipientAlreadyExistsException_whenSavingExistingRecipientButDifferentIBAN() {
        var recipientMock = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        addRecipientRepository.add(recipientMock);

        var recipientDifferentIban = RecipientTestFactory.createRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_PATRIZIO);
        doThrow(ConditionalCheckFailedException.class)
                .when(dynamoDbTable).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(recipientMock);

        var recipientAlreadyExistsException = assertThrows(RecipientAlreadyExistsException.class,
                () -> addRecipientRepository.add(recipientDifferentIban));

        assertThat(recipientAlreadyExistsException)
                .satisfies(exception -> {
                    assertThat(exception.getMessage()).isEqualTo("recipient.alreadyExists");
                    assertThat(exception.getBankAccountId()).isEqualTo(recipientMock.getBankAccountId());
                    assertThat(exception.getRecipientName()).isEqualTo(recipientMock.getRecipientName());
                });

        verify(dynamoDbTable, times(2)).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());
        verify(dynamoDbTable).getItem(any(Key.class));
        verifyNoMoreInteractions(dynamoDbTable);
    }
}
