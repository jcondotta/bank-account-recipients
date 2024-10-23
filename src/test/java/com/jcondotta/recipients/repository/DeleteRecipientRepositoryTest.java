package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientNotFoundException;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipientRepositoryTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    @InjectMocks
    private DeleteRecipientRepository deleteRecipientRepository;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Mock
    private Recipient recipient;

    @Test
    void shouldDeleteRecipient_whenRecipientExists() {
        deleteRecipientRepository.deleteRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        verify(dynamoDbTable).deleteItem(any(DeleteItemEnhancedRequest.class));
        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldThrowRecipientNotFoundException_whenDeletingNonExistentRecipient() {
        doThrow(ConditionalCheckFailedException.class)
                .when(dynamoDbTable).deleteItem(any(DeleteItemEnhancedRequest.class));

        var recipientNotFoundException = assertThrows(RecipientNotFoundException.class,
                () -> deleteRecipientRepository.deleteRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON));

        assertThat(recipientNotFoundException)
                .hasMessage("recipient.notFound")
                .satisfies(exception -> {
                    assertThat(exception.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
                    assertThat(exception.getRecipientName()).isEqualTo(RECIPIENT_NAME_JEFFERSON);
                });

        verify(dynamoDbTable).deleteItem(any(DeleteItemEnhancedRequest.class));
        verifyNoMoreInteractions(dynamoDbTable);
    }
}
