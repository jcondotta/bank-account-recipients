package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.argument_provider.validation.query_params.QueryParamsArgumentProvider;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.StringJoiner;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RecipientsCacheKeyTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () -> new RecipientsCacheKey(null));

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("cache.recipients.bankAccountId.notNull"));
    }

    @Test
    void shouldReturnCacheKey_whenNoQueryParamsIsProvided() {
        var recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL);

        var expectedCacheKey = buildExpectedCacheKey(BANK_ACCOUNT_ID_BRAZIL, recipientsCacheKey.queryParams());

        assertThat(recipientsCacheKey.getCacheKey()).isEqualTo(expectedCacheKey);
    }

    @ParameterizedTest
    @ArgumentsSource(QueryParamsArgumentProvider.class)
    void shouldReturnCacheKey_whenQueryParamsProvidedIsValid(QueryParams queryParams) {
        var recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        var expectedCacheKey = buildExpectedCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams);

        assertThat(recipientsCacheKey.getCacheKey()).isEqualTo(expectedCacheKey);
    }

    private String buildExpectedCacheKey(UUID bankAccountId, QueryParams queryParams){
        return new StringJoiner(":")
                .add("recipients")
                .add("bank-account-id:" + bankAccountId)
                .add("query-params:" + getHashSHA256(queryParams))
                .toString();
    }

    private String getHashSHA256(QueryParams queryParams) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(queryParams.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

}