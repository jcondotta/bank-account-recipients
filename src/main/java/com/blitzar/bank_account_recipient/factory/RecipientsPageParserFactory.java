package com.blitzar.bank_account_recipient.factory;

import com.blitzar.bank_account_recipient.service.query.parser.LastEvaluatedKeyParser;
import com.blitzar.bank_account_recipient.service.query.parser.RecipientPageParser;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class RecipientsPageParserFactory {

    @Singleton
    RecipientPageParser recipientPageParser(LastEvaluatedKeyParser lastEvaluatedKeyParser) {
        return new RecipientPageParser(lastEvaluatedKeyParser);
    }

    @Singleton
    LastEvaluatedKeyParser lastEvaluatedKeyParser() {
        return new LastEvaluatedKeyParser();
    }
}
