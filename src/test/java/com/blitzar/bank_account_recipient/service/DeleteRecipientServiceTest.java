package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipientServiceTest {

    private DeleteRecipientService deleteRecipientService;

    @Mock
    private RecipientRepository recipientRepositoryMock;

    @BeforeEach
    public void beforeEach(){
        deleteRecipientService = new DeleteRecipientService(recipientRepositoryMock);
    }

    @Test
    public void givenExistentRecipient_whenDeleteRecipient_thenDelete(){
        Recipient recipientMock = mock(Recipient.class);

        when(recipientRepositoryMock.find(anyLong(), anyString())).thenReturn(Optional.of(recipientMock));

        deleteRecipientService.deleteRecipient(anyLong(), anyString());
        verify(recipientRepositoryMock).delete(any());
    }

    @Test
    public void givenNonExistentRecipient_whenDeleteRecipient_thenThrowException(){
        var recipientId = "65a4557f95588135b2948184";
        var bankAccountId = 300L;
        when(recipientRepositoryMock.find(bankAccountId, recipientId)).thenReturn(Optional.empty());

        var exception = assertThrowsExactly(ResourceNotFoundException.class, () -> deleteRecipientService.deleteRecipient(bankAccountId, recipientId));

        assertAll(
                () -> assertThat(exception.getMessage()).isEqualTo("No recipient has been found with id: " + recipientId + " related to bank account: " + bankAccountId)
        );

        verify(recipientRepositoryMock, never()).delete(any());
    }
}