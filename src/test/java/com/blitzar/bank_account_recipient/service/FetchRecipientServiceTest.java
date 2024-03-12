package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchRecipientServiceTest {

    private FetchRecipientService fetchRecipientService;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Mock
    private PageIterable<Recipient> pageIterable;

    private Long bankAccountId = 998372L;
    private String recipientName = "Jefferson Condotta";
    private String recipientIBAN = "DE00 0000 0000 0000 00";
    private LocalDateTime currentDateTime = LocalDateTime.of(2022, Month.APRIL, 22, 10, 10, 10);

    @BeforeEach
    public void beforeEach(){
        fetchRecipientService = new FetchRecipientService(dynamoDbTable);
    }

    @Test
    public void givenExistentRecipients_whenGetRecipientByBankAccountId_thenReturnRecipients(){
        var recipient = new Recipient(bankAccountId, recipientName, recipientIBAN, currentDateTime);

        when(dynamoDbTable.query(any(QueryConditional.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(() -> Collections.singleton(recipient).iterator());

        RecipientsDTO recipientsDTO = fetchRecipientService.findRecipients(bankAccountId);
        assertThat(recipientsDTO.recipients()).hasSize(1);

        recipientsDTO.recipients().stream()
            .findFirst()
            .ifPresent(recipientDTO -> assertAll(
                    () -> assertThat(recipientDTO.bankAccountId()).isEqualTo(recipient.getBankAccountId()),
                    () -> assertThat(recipientDTO.name()).isEqualTo(recipient.getName()),
                    () -> assertThat(recipientDTO.iban()).isEqualTo(recipient.getIban()),
                    () -> assertThat(recipientDTO.createdAt()).isEqualTo(recipient.getCreatedAt())
            ));
    }

    @Test
    public void givenNonExistentRecipients_whenGetRecipientByBankAccountId_thenReturnEmptyList(){
        var nonExistentBankAccountId = NumberUtils.INTEGER_MINUS_ONE.longValue();

        when(dynamoDbTable.query(any(QueryConditional.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(() -> Collections.emptyIterator());

        RecipientsDTO recipientsDTO = fetchRecipientService.findRecipients(nonExistentBankAccountId);

        assertAll(
            () -> assertThat(recipientsDTO).isNotNull(),
            () -> assertThat(recipientsDTO.recipients()).hasSize(0)
        );
    }
}