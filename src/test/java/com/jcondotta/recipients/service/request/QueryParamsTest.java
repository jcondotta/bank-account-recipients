package com.jcondotta.recipients.service.request;

import com.jcondotta.recipients.argument_provider.validation.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.BlankValuesArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
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
import org.testcontainers.shaded.com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class QueryParamsTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnCorrectToString_whenQueryParamsIsValid(QueryParams queryParams) {
        String expectedToString = "QueryParams{" +
                "recipientName=" + queryParams.recipientName() +
                ", limit=" + queryParams.limit() +
                ", lastEvaluatedKey=" + queryParams.lastEvaluatedKey() +
                "}";

        assertThat(queryParams).hasToString(expectedToString);
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnCorrectHash_whenQueryParamsIsValid(QueryParams queryParams) {
        String expectedHash = buildQueryParamsHash(queryParams);

        assertThat(queryParams.getHashSHA256()).isEqualTo(expectedHash);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldNotDetectConstraintViolation_whenRecipientNameIsBlank(String blankRecipientName) {
        final var queryParams = QueryParams.builder().withRecipientName(blankRecipientName).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void shouldDetectConstraintViolation_whenRecipientNameExceeds50Characters() {
        final var veryLongRecipientName = "J".repeat(51);
        final var queryParams = QueryParams.builder().withRecipientName(veryLongRecipientName).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("query.params.recipientName.tooLong");
                    assertThat(violation.getPropertyPath()).hasToString("recipientName");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldDetectConstraintViolation_whenRecipientNameIsMalicious(String maliciousRecipientName) {
        final var queryParams = QueryParams.builder().withRecipientName(maliciousRecipientName).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("query.params.recipientName.invalid");
                    assertThat(violation.getPropertyPath()).hasToString("recipientName");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenLimitIsBelow1() {
        var queryParams = QueryParams.builder().withLimit(0).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("query.params.limit.minimum");
                    assertThat(violation.getPropertyPath()).hasToString("limit");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenLimitExceeds20() {
        var queryParams = QueryParams.builder().withLimit(21).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("query.params.limit.exceedsMaximum");
                    assertThat(violation.getPropertyPath()).hasToString("limit");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenLastEvaluatedKeyBankAccountIdIsNull() {
        final var lastEvaluatedKey = new LastEvaluatedKey(null, RECIPIENT_NAME_JEFFERSON);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("lastEvaluatedKey.bankAccountId");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldDetectConstraintViolation_whenLastEvaluatedKeyRecipientNameIsBlank(String blankRecipientName) {
        final var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, blankRecipientName);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("lastEvaluatedKey.recipientName");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenLastEvaluatedKeyRecipientNameExceeds50Characters() {
        final var veryLongRecipientName = "J".repeat(51);
        final var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, veryLongRecipientName);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.tooLong");
                    assertThat(violation.getPropertyPath()).hasToString("lastEvaluatedKey.recipientName");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldDetectConstraintViolation_whenLastEvaluatedKeyRecipientNameIsMalicious(String maliciousRecipientName) {
        final var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, maliciousRecipientName);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();

        var constraintViolations = VALIDATOR.validate(queryParams);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.invalid");
                    assertThat(violation.getPropertyPath()).hasToString("lastEvaluatedKey.recipientName");
                });
    }

    private String buildQueryParamsHash(QueryParams queryParams){
        return Hashing.sha256()
                .hashString(queryParams.toString(), StandardCharsets.UTF_8)
                .toString();
    }
}