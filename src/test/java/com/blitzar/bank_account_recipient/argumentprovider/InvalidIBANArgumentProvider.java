package com.blitzar.bank_account_recipient.argumentprovider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidIBANArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                // Too short IBAN
                "DE1234567",
                // Too long IBAN
                "DE44 1234 5678 9012 3456 7890 1234 5678",
                // Invalid country code
                "XX44 1234 5678 9012 3456 78",
                // Missing IBAN sections
                "DE44 1234",
                // IBAN with invalid characters
                "DE44 1234 5678 9012 3456 7@",
                // Completely malformed IBAN
                "123ABC!"
        ).map(Arguments::of);
    }
}