package com.jcondotta.recipients.service.request;

import com.jcondotta.recipients.argument_provider.validation.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.security.ThreatInputArgumentProvider;
import com.jcondotta.recipients.factory.ValidatorTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import jakarta.validation.Validator;
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

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @Test
    void shouldNotDetectConstraintViolation_whenLastEvaluatedKeyIsValid() {
        final var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        var constraintViolations = VALIDATOR.validate(lastEvaluatedKey);
        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void shouldDetectConstraintViolation_whenBankAccountIdIsNull() {
        final var lastEvaluatedKey = new LastEvaluatedKey(null, RECIPIENT_NAME_JEFFERSON);

        var constraintViolations = VALIDATOR.validate(lastEvaluatedKey);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("bankAccountId");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldDetectConstraintViolation_whenRecipientNameIsBlank(String blankRecipientName) {
        final var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, blankRecipientName);

        var constraintViolations = VALIDATOR.validate(lastEvaluatedKey);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("recipientName");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenRecipientNameExceeds50Characters() {
        final var veryLongRecipientName = "J".repeat(51);
        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, veryLongRecipientName);

        var constraintViolations = VALIDATOR.validate(lastEvaluatedKey);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.tooLong");
                    assertThat(violation.getPropertyPath()).hasToString("recipientName");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldDetectConstraintViolation_whenRecipientNameIsMalicious(String maliciousRecipientName) {
        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, maliciousRecipientName);

        var constraintViolations = VALIDATOR.validate(lastEvaluatedKey);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.invalid");
                    assertThat(violation.getPropertyPath()).hasToString("recipientName");
                });
    }

    @Test
    void shouldReturnExclusiveStartKey_whenKeyValuesAreValid() {
        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        Map<String, AttributeValue> exclusiveStartKey = lastEvaluatedKey.toExclusiveStartKey();

        assertThat(exclusiveStartKey).hasSize(2);
        assertThat(exclusiveStartKey.get("bankAccountId").s()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL.toString());
        assertThat(exclusiveStartKey.get("recipientName").s()).isEqualTo(RECIPIENT_NAME_JEFFERSON);
    }

    @Test
    void shouldThrowNullPointerException_whenGetExclusiveStartKeyWithNullBankAccountId() {
        var lastEvaluatedKey = new LastEvaluatedKey(null, RECIPIENT_NAME_JEFFERSON);

        var exception = assertThrows(NullPointerException.class, () -> lastEvaluatedKey.toExclusiveStartKey());
        assertThat(exception).hasMessage("lastEvaluatedKey.bankAccountId.notNull");
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldThrowNullPointerException_whenGetExclusiveStartKeyWithBlankBankAccountId(String invalidRecipientName) {
        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName);

        var exception = assertThrows(IllegalArgumentException.class, () -> lastEvaluatedKey.toExclusiveStartKey());
        assertThat(exception).hasMessage("lastEvaluatedKey.recipientName.notBlank");
    }

    @Test
    void shouldReturnConsistentToStringAndHashCode_whenValuesAreIdentical() {
        LastEvaluatedKey lastEvaluatedKey1 = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        LastEvaluatedKey lastEvaluatedKey2 = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        assertEquals(lastEvaluatedKey1.toString(), lastEvaluatedKey2.toString());
        assertEquals(lastEvaluatedKey1.hashCode(), lastEvaluatedKey2.hashCode());
    }
}
