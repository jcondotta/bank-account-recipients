package com.jcondotta.recipients.service.request;

import com.jcondotta.recipients.argument_provider.validation.BlankAndNonPrintableCharactersArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.BlankValuesArgumentProvider;
import com.jcondotta.recipients.argument_provider.validation.security.ThreatInputArgumentProvider;
import com.jcondotta.recipients.factory.ValidatorTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QueryRecipientsRequestTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    private QueryRecipientsRequest queryRecipientsRequest;

    @Test
    void shouldReturnValidQueryRecipientsRequest_whenAllParamsAreProvided() {
        var queryParams = QueryParams.builder()
                .withRecipientName(RECIPIENT_NAME_JEFFERSON)
                .withLimit(20)
                .withLastEvaluatedKey(new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON))
                .build();

        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        assertThat(queryRecipientsRequest.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
        assertThat(queryRecipientsRequest.queryParams())
                .usingRecursiveAssertion()
                .isEqualTo(queryParams);
    }

    @Test
    void shouldReturnValidQueryRecipientsRequest_whenNoQueryParamsIsProvided() {
        var queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL);

        var expectedQueryParams = QueryParams.builder().build();
        assertThat(queryRecipientsRequest.queryParams()).isEqualTo(expectedQueryParams);
    }

    @Test
    void shouldSetEmptyQueryParams_whenQueryParamsIsNull() {
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, null);
        assertThat(queryRecipientsRequest.queryParams()).isNotNull();
        assertThat(queryRecipientsRequest.queryParams().recipientName()).isEmpty();
        assertThat(queryRecipientsRequest.queryParams().limit()).isEmpty();
        assertThat(queryRecipientsRequest.queryParams().lastEvaluatedKey()).isEmpty();

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () -> new QueryRecipientsRequest(null));

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("query.recipients.bankAccountId.notNull"));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldNotDetectConstraintViolation_whenQueryParamsRecipientNameIsBlank(String blankRecipientName) {
        final var queryParams = QueryParams.builder().withRecipientName(blankRecipientName).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations).isEmpty();
    }

    @Test
    void shouldDetectConstraintViolation_whenQueryParamsRecipientNameExceeds50Characters() {
        final var veryLongRecipientName = "J".repeat(51);
        final var queryParams = QueryParams.builder().withRecipientName(veryLongRecipientName).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("query.params.recipientName.tooLong");
                    assertThat(violation.getPropertyPath()).hasToString("queryParams.recipientName");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldDetectConstraintViolation_whenQueryParamsRecipientNameIsMalicious(String maliciousRecipientName) {
        final var queryParams = QueryParams.builder().withRecipientName(maliciousRecipientName).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("query.params.recipientName.invalid");
                    assertThat(violation.getPropertyPath()).hasToString("queryParams.recipientName");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenQueryParamsLimitIsBelow1() {
        var queryParams = QueryParams.builder().withLimit(0).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("query.params.limit.minimum");
                    assertThat(violation.getPropertyPath()).hasToString("queryParams.limit");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenQueryParamsLimitExceeds20() {
        var queryParams = QueryParams.builder().withLimit(21).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("query.params.limit.exceedsMaximum");
                    assertThat(violation.getPropertyPath()).hasToString("queryParams.limit");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenQueryParamsLastEvaluatedKeyBankAccountIdIsNull() {
        final var lastEvaluatedKey = new LastEvaluatedKey(null, RECIPIENT_NAME_JEFFERSON);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.bankAccountId.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("queryParams.lastEvaluatedKey.bankAccountId");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    void shouldDetectConstraintViolation_whenQueryParamsLastEvaluatedKeyRecipientNameIsBlank(String blankRecipientName) {
        final var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, blankRecipientName);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("queryParams.lastEvaluatedKey.recipientName");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenQueryParamsLastEvaluatedKeyRecipientNameExceeds50Characters() {
        final var veryLongRecipientName = "J".repeat(51);
        final var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, veryLongRecipientName);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.tooLong");
                    assertThat(violation.getPropertyPath()).hasToString("queryParams.lastEvaluatedKey.recipientName");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldDetectConstraintViolation_whenQueryParamsLastEvaluatedKeyRecipientNameIsMalicious(String maliciousRecipientName) {
        final var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, maliciousRecipientName);
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();
        queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var constraintViolations = VALIDATOR.validate(queryRecipientsRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("lastEvaluatedKey.recipientName.invalid");
                    assertThat(violation.getPropertyPath()).hasToString("queryParams.lastEvaluatedKey.recipientName");
                });
    }
}