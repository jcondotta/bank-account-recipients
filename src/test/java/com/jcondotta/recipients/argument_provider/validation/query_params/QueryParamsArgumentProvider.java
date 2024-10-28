package com.jcondotta.recipients.argument_provider.validation.query_params;

import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import com.jcondotta.recipients.service.request.QueryParams;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.UUID;
import java.util.stream.Stream;

public class QueryParamsArgumentProvider implements ArgumentsProvider {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final int DEFAULT_LIMIT = 10;
    private static final LastEvaluatedKey LAST_EVALUATED_KEY = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(Named.of("empty queryParams", QueryParams.builder()
                        .build())),

                Arguments.of(Named.of("with recipientName", QueryParams.builder()
                        .withRecipientName(RECIPIENT_NAME_JEFFERSON)
                        .build())),

                Arguments.of(Named.of("with page limit", QueryParams.builder()
                        .withLimit(DEFAULT_LIMIT)
                        .build())),

                Arguments.of(Named.of("with last evaluated key", QueryParams.builder()
                        .withLastEvaluatedKey(LAST_EVALUATED_KEY)
                        .build())),

                Arguments.of(Named.of("with recipientName and limit", QueryParams.builder()
                        .withRecipientName(RECIPIENT_NAME_JEFFERSON)
                        .build())),

                Arguments.of(Named.of("with recipientName and last evaluated key", QueryParams.builder()
                        .withRecipientName(RECIPIENT_NAME_JEFFERSON)
                        .withLastEvaluatedKey(LAST_EVALUATED_KEY)
                        .build())),

                Arguments.of(Named.of("with limit and last evaluated key", QueryParams.builder()
                        .withLimit(DEFAULT_LIMIT)
                        .withLastEvaluatedKey(LAST_EVALUATED_KEY)
                        .build())),

                Arguments.of(Named.of("with all values set", QueryParams.builder()
                        .withRecipientName(RECIPIENT_NAME_JEFFERSON)
                        .withLimit(DEFAULT_LIMIT)
                        .withLastEvaluatedKey(LAST_EVALUATED_KEY)
                        .build()))
        );
    }
}
