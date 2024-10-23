package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientAlreadyExistsException;
import com.jcondotta.recipients.exception.RecipientNotFoundException;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientRepositoryTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    @InjectMocks
    private RecipientRepository recipientRepository;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Mock
    private Recipient recipient;

    @Test
    void shouldSaveRecipient_whenRecipientIsValid() {
        recipientRepository.saveRecipient(recipient);

        var argumentCaptor = ArgumentCaptor.forClass(PutItemEnhancedRequest.class);
        verify(dynamoDbTable).putItem(argumentCaptor.capture());

        var argumentCaptorValue = argumentCaptor.getValue();
        assertThat(argumentCaptorValue.item()).isEqualTo(recipient);

        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldThrowRecipientAlreadyExistsException_whenSavingExistingRecipient() {
        doThrow(ConditionalCheckFailedException.class)
                .when(dynamoDbTable).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());

        when(recipient.getBankAccountId()).thenReturn(BANK_ACCOUNT_ID_BRAZIL);
        when(recipient.getRecipientName()).thenReturn(RECIPIENT_NAME_JEFFERSON);

        var recipientAlreadyExistsException = assertThrows(RecipientAlreadyExistsException.class,
                () -> recipientRepository.saveRecipient(recipient));

        assertThat(recipientAlreadyExistsException)
                .satisfies(exception -> {
                    assertThat(exception.getMessage()).isEqualTo("recipient.alreadyExists");
                    assertThat(exception.getBankAccountId()).isEqualTo(recipient.getBankAccountId());
                    assertThat(exception.getRecipientName()).isEqualTo(recipient.getRecipientName());
                });

        verify(dynamoDbTable).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());
        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldDeleteRecipient_whenRecipientExists() {
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(recipient);

        recipientRepository.deleteRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        verify(dynamoDbTable).deleteItem(any(Recipient.class));
        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldThrowRecipientNotFoundException_whenDeletingNonExistentRecipient() {
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(null);

        var recipientNotFoundException = assertThrows(RecipientNotFoundException.class,
                () -> recipientRepository.deleteRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON));

        assertThat(recipientNotFoundException)
                .hasMessage("recipient.notFound")
                .satisfies(exception -> {
                    assertThat(exception.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
                    assertThat(exception.getRecipientName()).isEqualTo(RECIPIENT_NAME_JEFFERSON);
                });

        verify(dynamoDbTable, never()).deleteItem(any(Recipient.class));
        verifyNoMoreInteractions(dynamoDbTable);
    }
}
