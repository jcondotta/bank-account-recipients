package com.blitzar.bank_account_recipient.argumentprovider;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class NonPrintableCharactersArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
//                Arguments.of(Named.of("Null character (\\u0000)", "\u0000")),
//                Arguments.of(Named.of("Bell character (\\u0007)", "\u0007")),
//                Arguments.of(Named.of("Backspace character (\\u0008)", "\u0008")),
                Arguments.of(Named.of("Horizontal tab (\\u0009)", "\u0009")),
                Arguments.of(Named.of("Vertical tab (\\u000B)", "\u000B")),
                Arguments.of(Named.of("Form feed character (\\u000C)", "\u000C")),
                Arguments.of(Named.of("Newline character (\\n)", StringUtils.LF)),
                Arguments.of(Named.of("Carriage return (\\r)", StringUtils.CR)),
//                Arguments.of(Named.of("Delete character (\\u007F)", "\u007F")),
                Arguments.of(Named.of("Unit separator (\\u001F)", "\u001F"))
//                Arguments.of(Named.of("Substitute character (\\u001A)", "\u001A"))
        );
    }
}