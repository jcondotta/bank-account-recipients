package com.jcondotta.recipients.service.request;

import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.factory.ValidatorTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class QueryParamsTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final int DEFAULT_LIMIT = 10;
    private static final LastEvaluatedKey LAST_EVALUATED_KEY = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

    @Test
    void shouldReturnCorrectRecipientName_whenRecipientNameIsProvided() {
        var queryParams = QueryParams.builder()
                .withRecipientName(RECIPIENT_NAME_JEFFERSON)
                .build();

        assertThat(queryParams.recipientName()).isPresent()
                .isEqualTo(Optional.of(RECIPIENT_NAME_JEFFERSON));
    }

    @Test
    void shouldReturnEmptyRecipientName_whenRecipientNameIsNotProvided() {
        var queryParams = QueryParams.builder().build();
        assertThat(queryParams.recipientName()).isNotPresent();
    }

    @Test
    void shouldReturnCorrectLimit_whenLimitIsProvided() {
        var queryParams = QueryParams.builder().withLimit(DEFAULT_LIMIT).build();

        assertThat(queryParams.limit()).isPresent()
                .isEqualTo(Optional.of(DEFAULT_LIMIT));
    }

    @Test
    void shouldThrowConstraintViolationException_whenBankAccountIdIsNull() {
        final Validator VALIDATOR = ValidatorTestFactory.getValidator();
        var queryParams = QueryParams.builder().withLimit(21).build();

        Set<ConstraintViolation<QueryParams>> validate = VALIDATOR.validate(queryParams);
        System.out.println(validate);

        assertThat(queryParams.limit()).isPresent()
                .isEqualTo(Optional.of(DEFAULT_LIMIT));
    }

    @Test
    void shouldReturnEmptyLimit_whenLimitIsNotProvided() {
        var queryParams = QueryParams.builder().build();
        assertThat(queryParams.limit()).isNotPresent();
    }

    @Test
    void shouldReturnCorrectLastEvaluatedKey_whenLastEvaluatedKeyIsProvided() {
        var queryParams = QueryParams.builder().withLastEvaluatedKey(LAST_EVALUATED_KEY).build();

        assertThat(queryParams.lastEvaluatedKey()).isPresent()
                .isEqualTo(Optional.of(LAST_EVALUATED_KEY));
    }

    @Test
    void shouldReturnEmptyLastEvaluatedKey_whenLastEvaluatedKeyIsNotProvided() {
        var queryParams = QueryParams.builder().build();
        assertThat(queryParams.lastEvaluatedKey()).isNotPresent();
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnCorrectHash_whenQueryParamsIsValid(QueryParams queryParams) {
        String expectedHash = buildQueryParamsHash(queryParams);

        assertThat(queryParams.getHashSHA256())
                .isEqualTo(expectedHash);
    }

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

    private String buildQueryParamsHash(QueryParams queryParams){
        return Hashing.sha256()
                .hashString(queryParams.toString(), StandardCharsets.UTF_8)
                .toString();
    }
}