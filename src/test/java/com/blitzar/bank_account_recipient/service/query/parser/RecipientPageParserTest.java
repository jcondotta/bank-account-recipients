package com.blitzar.bank_account_recipient.service.query.parser;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.factory.RecipientTestFactory;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.query.parser.LastEvaluatedKeyParser;
import com.blitzar.bank_account_recipient.service.query.parser.RecipientPageParser;
import com.blitzar.bank_account_recipient.service.request.LastEvaluatedKey;
import com.blitzar.bank_account_recipient.validation.recipient.RecipientsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientPageParserTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private final RecipientsValidator recipientsValidator = new RecipientsValidator();

    @Mock
    private LastEvaluatedKeyParser lastEvaluatedKeyParser;

    @Mock
    private Page<Recipient> recipientsPage;

    private RecipientPageParser recipientPageParser;

    @BeforeEach
    void beforeEach() {
        recipientPageParser = new RecipientPageParser(lastEvaluatedKeyParser);
    }

    @Test
    void shouldReturnEmptyRecipientsDTO_whenRecipientsPageIsNull() {
        var recipientsDTO = recipientPageParser.parse(null);

        assertThat(recipientsDTO)
                .satisfies(dto -> {
                    assertThat(dto.recipients()).isEmpty();
                    assertThat(dto.count()).isZero();
                    assertThat(dto.lastEvaluatedKey()).isNull();
                });

        verify(lastEvaluatedKeyParser, never()).parse(recipientsPage);
    }

    @Test
    void shouldReturnEmptyRecipientsDTO_whenRecipientsPageItemsIsNull() {
        when(recipientsPage.items()).thenReturn(null);
        var recipientsDTO = recipientPageParser.parse(recipientsPage);

        assertThat(recipientsDTO)
                .satisfies(dto -> {
                    assertThat(dto.recipients()).isEmpty();
                    assertThat(dto.count()).isZero();
                    assertThat(dto.lastEvaluatedKey()).isNull();
                });

        verify(lastEvaluatedKeyParser, never()).parse(recipientsPage);
    }

    @Test
    void shouldReturnEmptyRecipientsDTO_whenRecipientsPageItemsIsEmpty() {
        when(recipientsPage.items()).thenReturn(Collections.emptyList());
        var recipientsDTO = recipientPageParser.parse(recipientsPage);

        assertThat(recipientsDTO)
                .satisfies(dto -> {
                    assertThat(dto.recipients()).isEmpty();
                    assertThat(dto.count()).isZero();
                    assertThat(dto.lastEvaluatedKey()).isNull();
                });

        verify(lastEvaluatedKeyParser).parse(recipientsPage);
    }

    @Test
    void shouldReturnRecipientsDTO_whenRecipientsAreFoundAndLastEvaluatedKeyNull() {
        var recipients = RecipientTestFactory.createRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.JESSICA);
        when(recipientsPage.items()).thenReturn(recipients);

        var recipientsDTO = recipientPageParser.parse(recipientsPage);

        assertThat(recipientsDTO)
                .satisfies(dto -> {
                    assertThat(dto.recipients()).hasSize(recipients.size());
                    assertThat(dto.count()).isEqualTo(recipients.size());
                    assertThat(dto.lastEvaluatedKey()).isNull();
                });
        recipientsValidator.validateEntitiesAgainstDTOs(recipients, recipientsDTO.recipients());

        verify(lastEvaluatedKeyParser).parse(recipientsPage);
    }

    @Test
    void shouldReturnRecipientsDTO_whenRecipientsAreFoundAndLastEvaluatedKeyIsEmpty() {
        var recipients = RecipientTestFactory.createRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.JESSICA);
        when(recipientsPage.items()).thenReturn(recipients);

        var recipientsDTO = recipientPageParser.parse(recipientsPage);

        assertThat(recipientsDTO)
                .satisfies(dto -> {
                    assertThat(dto.recipients()).hasSize(recipients.size());
                    assertThat(dto.count()).isEqualTo(recipients.size());
                    assertThat(dto.lastEvaluatedKey()).isNull();
                });
        recipientsValidator.validateEntitiesAgainstDTOs(recipients, recipientsDTO.recipients());

        verify(lastEvaluatedKeyParser).parse(recipientsPage);
    }

    @Test
    void shouldReturnRecipientsDTO_whenRecipientsAreFoundAndLastEvaluatedKeyNotNull() {
        var recipients = RecipientTestFactory.createRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.JESSICA);
        when(recipientsPage.items()).thenReturn(recipients);

        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        when(lastEvaluatedKeyParser.parse(recipientsPage))
                .thenReturn(lastEvaluatedKey);

        var recipientsDTO = recipientPageParser.parse(recipientsPage);

        assertThat(recipientsDTO)
                .satisfies(dto -> {
                    assertThat(dto.recipients()).hasSize(recipients.size());
                    assertThat(dto.count()).isEqualTo(recipients.size());
                    assertThat(dto.lastEvaluatedKey().bankAccountId()).isEqualTo(lastEvaluatedKey.bankAccountId());
                    assertThat(dto.lastEvaluatedKey().recipientName()).isEqualTo(lastEvaluatedKey.recipientName());
                });
        recipientsValidator.validateEntitiesAgainstDTOs(recipients, recipientsDTO.recipients());

        verify(lastEvaluatedKeyParser).parse(recipientsPage);
    }
}
