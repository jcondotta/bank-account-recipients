package com.jcondotta.recipients.argument_provider.validation;

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
                Arguments.of(Named.of("Tab String", "\t")),
                Arguments.of(Named.of("Horizontal tab (\\u0009)", "\u0009")),
                Arguments.of(Named.of("Vertical tab (\\u000B)", "\u000B")),
                Arguments.of(Named.of("Form feed character (\\u000C)", "\u000C")),
                Arguments.of(Named.of("Newline character (\\n)", StringUtils.LF)),
                Arguments.of(Named.of("Carriage return (\\r)", StringUtils.CR)),
                Arguments.of(Named.of("Unit separator (\\u001F)", "\u001F"))
        );
    }
}