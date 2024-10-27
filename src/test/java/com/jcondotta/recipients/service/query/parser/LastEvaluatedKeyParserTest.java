package com.jcondotta.recipients.service.query.parser;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LastEvaluatedKeyParserTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private static final Map.Entry<String, AttributeValue> BANK_ACCOUNT_ID_BRAZIL_ENTRY =
            Map.entry("bankAccountId", AttributeValue.fromS(BANK_ACCOUNT_ID_BRAZIL.toString()));

    private static final Map.Entry<String, AttributeValue> RECIPIENT_NAME_JEFFERSON_ENTRY =
            Map.entry("recipientName", AttributeValue.fromS(RECIPIENT_NAME_JEFFERSON));

    @Mock
    private Page<Recipient> pageRecipient;

    private LastEvaluatedKeyParser lastEvaluatedKeyParser;

    @BeforeEach
    void beforeEach() {
        lastEvaluatedKeyParser = new LastEvaluatedKeyParser();
    }

    @Test
    void shouldReturnLastEvaluatedKey_whenPageContainsValidAttributes() {
        var lastEvaluatedKeyMap = Map.ofEntries(BANK_ACCOUNT_ID_BRAZIL_ENTRY, RECIPIENT_NAME_JEFFERSON_ENTRY);
        when(pageRecipient.lastEvaluatedKey()).thenReturn(lastEvaluatedKeyMap);

        var expectedLastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        LastEvaluatedKey lastEvaluatedKey = lastEvaluatedKeyParser.parse(pageRecipient);

        assertThat(lastEvaluatedKey.bankAccountId()).isEqualTo(expectedLastEvaluatedKey.bankAccountId());
        assertThat(lastEvaluatedKey.recipientName()).isEqualTo(expectedLastEvaluatedKey.recipientName());
    }

    @Test
    void shouldThrowException_whenBankAccountIdIsMissing() {
        var lastEvaluatedKeyMap = Map.ofEntries(RECIPIENT_NAME_JEFFERSON_ENTRY);
        when(pageRecipient.lastEvaluatedKey()).thenReturn(lastEvaluatedKeyMap);

        var exception = assertThrows(IllegalStateException.class, () -> lastEvaluatedKeyParser.parse(pageRecipient));
        assertThat(exception).hasMessage("Missing bankAccountId in last evaluated key.");
    }

    @Test
    void shouldThrowException_whenRecipientNameIsMissing() {
        var lastEvaluatedKeyMap = Map.ofEntries(BANK_ACCOUNT_ID_BRAZIL_ENTRY);
        when(pageRecipient.lastEvaluatedKey()).thenReturn(lastEvaluatedKeyMap);

        var exception = assertThrows(IllegalStateException.class, () -> lastEvaluatedKeyParser.parse(pageRecipient));
        assertThat(exception).hasMessage("Missing recipientName in last evaluated key.");
    }

    @Test
    void shouldThrowException_whenBankAccountIdHasInvalidFormat() {
        Map.Entry<String, AttributeValue> invalidBankAccountIdEntry =
                Map.entry("bankAccountId", AttributeValue.fromS("invalidBankAccountId"));

        var lastEvaluatedKeyMap = Map.ofEntries(invalidBankAccountIdEntry, RECIPIENT_NAME_JEFFERSON_ENTRY);
        when(pageRecipient.lastEvaluatedKey()).thenReturn(lastEvaluatedKeyMap);

        var exception = assertThrows(IllegalStateException.class, () -> lastEvaluatedKeyParser.parse(pageRecipient));
        assertThat(exception).hasMessage("Invalid UUID format for bankAccountId in last evaluated key.");
    }

    @Test
    void shouldReturnNull_whenNoLastEvaluatedKeyIsFound() {
        when(pageRecipient.lastEvaluatedKey()).thenReturn(Map.of());

        LastEvaluatedKey lastEvaluatedKey = lastEvaluatedKeyParser.parse(pageRecipient);
        assertThat(lastEvaluatedKey).isNull();
    }

    @Test
    void shouldReturnNull_whenLastEvaluatedKeyIsNull() {
        when(pageRecipient.lastEvaluatedKey()).thenReturn(null);

        LastEvaluatedKey lastEvaluatedKey = lastEvaluatedKeyParser.parse(pageRecipient);
        assertThat(lastEvaluatedKey).isNull();
    }
}
