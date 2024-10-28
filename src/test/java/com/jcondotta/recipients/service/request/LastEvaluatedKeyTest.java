package com.jcondotta.recipients.service.request;

import com.jcondotta.recipients.argument_provider.validation.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class LastEvaluatedKeyTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    @Test
    void shouldReturnExclusiveStartKey_whenKeyValuesAreValid() {
        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        Map<String, AttributeValue> exclusiveStartKey = lastEvaluatedKey.toExclusiveStartKey();

        Assertions.assertThat(exclusiveStartKey).hasSize(2);
        Assertions.assertThat(exclusiveStartKey.get("bankAccountId").s()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL.toString());
        Assertions.assertThat(exclusiveStartKey.get("recipientName").s()).isEqualTo(RECIPIENT_NAME_JEFFERSON);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenExclusiveStartKeyHasNullBankAccountId() {
        var lastEvaluatedKey = new LastEvaluatedKey(null, RECIPIENT_NAME_JEFFERSON);

        var exception = assertThrows(IllegalArgumentException.class, () -> lastEvaluatedKey.toExclusiveStartKey());
        assertThat(exception)
                .hasMessage("bankAccountId must not be null");
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldThrowIllegalArgumentException_whenExclusiveStartKeyHasBlankRecipientName(String invalidRecipientName) {
        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName);

        var exception = assertThrows(IllegalArgumentException.class, () -> lastEvaluatedKey.toExclusiveStartKey());
        assertThat(exception)
                .hasMessage("recipientName must not be null or blank");
    }

    @Test
    void shouldReturnConsistentToStringAndHashCode_whenValuesAreIdentical() {
        LastEvaluatedKey lastEvaluatedKey1 = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        LastEvaluatedKey lastEvaluatedKey2 = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        assertEquals(lastEvaluatedKey1.toString(), lastEvaluatedKey2.toString());
        assertEquals(lastEvaluatedKey1.hashCode(), lastEvaluatedKey2.hashCode());
    }
}
