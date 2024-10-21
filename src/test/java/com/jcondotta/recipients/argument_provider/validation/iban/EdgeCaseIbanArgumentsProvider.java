package com.jcondotta.recipients.argument_provider.validation.iban;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class EdgeCaseIbanArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("Minimal Length IBAN (Norway)", "NO9386011117947")),
                Arguments.of(Named.of("Max Length IBAN (Malta)", "MT84MALT011000012345MTLCAST001S")),
                Arguments.of(Named.of("Valid German IBAN", "DE89370400440532013000")),
                Arguments.of(Named.of("Valid UK IBAN", "GB29NWBK60161331926819")),
                Arguments.of(Named.of("Valid Swiss IBAN", "CH9300762011623852957")),
                Arguments.of(Named.of("Valid French IBAN", "FR1420041010050500013M02606")),

                // Additional valid IBANs, possibly with formatting variations
                Arguments.of(Named.of("Valid IBAN with Spaces", "DE89 3704 0044 0532 0130 00")),
                Arguments.of(Named.of("Valid IBAN with Grouping", "GB29 NWBK 6016 1331 9268 19"))
        );
    }
}