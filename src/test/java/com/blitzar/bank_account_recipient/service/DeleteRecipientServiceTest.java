package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipientServiceTest {

    private DeleteRecipientService deleteRecipientService;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    private Long bankAccountId = 998372L;
    private String recipientName = "Jefferson Condotta";

    @BeforeEach
    public void beforeEach(){
        deleteRecipientService = new DeleteRecipientService(dynamoDbTable);
    }

    @Test
    public void givenExistentRecipient_whenDeleteRecipient_thenDelete(){
        Recipient recipientMock = mock(Recipient.class);

        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(recipientMock);

        deleteRecipientService.deleteRecipient(bankAccountId, recipientName);
        verify(dynamoDbTable).deleteItem(recipientMock);
    }

    @Test
    public void givenNonExistentRecipient_whenDeleteRecipient_thenThrowException(){
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(null);

        assertThatThrownBy(() -> deleteRecipientService.deleteRecipient(bankAccountId, recipientName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("[BankAccountId=" + bankAccountId + ", RecipientName=" + recipientName + "] No recipient has been found");

        verify(dynamoDbTable, never()).deleteItem(any(Recipient.class));
    }
}