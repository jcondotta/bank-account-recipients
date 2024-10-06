package com.blitzar.bank_account_recipient.argumentprovider;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BlankValuesArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("Tab Character (\\t or \\u0009)", "\t")),
                Arguments.of(Named.of("Vertical Tab (\\u000B)", "\u000B")),
                Arguments.of(Named.of("Form Feed (\\u000C)", "\u000C")),
                Arguments.of(Named.of("Newline (\\n)", StringUtils.LF)),
                Arguments.of(Named.of("Carriage Return (\\r)", StringUtils.CR)),
                Arguments.of(Named.of("Unit Separator (\\u001F)", "\u001F"))
        );
    }
}