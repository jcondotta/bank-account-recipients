package com.blitzar.bank_account_recipient.argumentprovider.validation.iban;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * Provides invalid IBAN scenarios for parameterized tests. This includes IBANs that are
 * too short, too long, have invalid country codes, or contain invalid characters.
 */
public class InvalidIbanArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("Too Short IBAN", "DE1234567")),
                Arguments.of(Named.of("Too Long IBAN", "DE44 1234 5678 9012 3456 7890 1234 5678")),
                Arguments.of(Named.of("Invalid Country Code", "XX44 1234 5678 9012 3456 78")),
                Arguments.of(Named.of("Missing IBAN Sections", "DE44 1234")),
                Arguments.of(Named.of("IBAN with Invalid Characters", "DE44 1234 5678 9012 3456 7@")),
                Arguments.of(Named.of("Completely Malformed IBAN", "123ABC!")),
                Arguments.of(Named.of("Lowercase Letters in IBAN", "de44 1234 5678 9012 3456 78")),
                Arguments.of(Named.of("Valid Country, Invalid Format", "DEAA 1234 5678 9012 3456 78")),
                Arguments.of(Named.of("IBAN with Spaces in Wrong Places", "DE44 12 34 567890123456 78"))
        );
    }
}