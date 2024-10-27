package com.jcondotta.recipients.service.request;

import com.jcondotta.recipients.helper.TestBankAccount;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QueryRecipientsRequestTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();

    @Test
    void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () -> new QueryRecipientsRequest(null));

        assertThat(exception)
                .satisfies(violation -> assertThat(violation.getMessage())
                        .isEqualTo("query.recipients.bankAccountId.notNull"));
    }

    @Test
    void shouldReturnValidQueryRecipientsRequest_whenNoQueryParamsIsProvided() {
        var queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL);

        assertThat(queryRecipientsRequest.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
        assertThat(queryRecipientsRequest.queryParams()).isEqualTo(QueryParams.builder().build());
    }

    @Test
    void shouldReturnValidQueryRecipientsRequest_whenAllParamsAreProvided() {
        var queryRecipientsRequest = new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, QueryParams.builder().build());

        assertThat(queryRecipientsRequest.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL);
        assertThat(queryRecipientsRequest.queryParams()).isEqualTo(QueryParams.builder().build());
    }
}