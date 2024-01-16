package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRecipientServiceTest {

    private FetchRecipientService fetchRecipientService;

    @Mock
    private RecipientRepository recipientRepositoryMock;

    private String recipientId = "65a3ee73f4c13d577b10195e";
    private String recipientName = "Jefferson Condotta";
    private String recipientIBAN = "DE00 0000 0000 0000 00";
    private Long bankAccountId = 998372L;

    @BeforeEach
    public void beforeEach(){
        fetchRecipientService = new FetchRecipientService(recipientRepositoryMock);
    }

    @Test
    public void givenExistentRecipients_whenGetRecipientByBankAccountId_thenReturnRecipients(){
        var recipient = new Recipient(recipientName, recipientIBAN, bankAccountId);
        recipient.setId(recipientId);

        when(recipientRepositoryMock.find(bankAccountId)).thenReturn(List.of(recipient));

        RecipientsDTO recipientsDTO = fetchRecipientService.findRecipients(bankAccountId);
        assertThat(recipientsDTO.recipients()).hasSize(1);

        recipientsDTO.recipients().stream()
            .findFirst()
            .ifPresent(recipientDTO -> assertAll(
                    () -> assertThat(recipient.getId()).isEqualTo(recipientId),
                    () -> assertThat(recipient.getName()).isEqualTo(recipient.getName()),
                    () -> assertThat(recipient.getIban()).isEqualTo(recipient.getIban()),
                    () -> assertThat(recipient.getBankAccountId()).isEqualTo(recipient.getBankAccountId())
            ));
    }

    @Test
    public void givenNonExistentRecipients_whenGetRecipientByBankAccountId_thenReturnEmptyList(){
        var nonExistentBankAccountId = NumberUtils.INTEGER_MINUS_ONE.longValue();
        when(recipientRepositoryMock.find(nonExistentBankAccountId)).thenReturn(Collections.EMPTY_LIST);

        RecipientsDTO recipientsDTO = fetchRecipientService.findRecipients(nonExistentBankAccountId);

        assertAll(
            () -> assertThat(recipientsDTO).isNotNull(),
            () -> assertThat(recipientsDTO.recipients()).hasSize(0)
        );
    }
}