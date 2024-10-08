package com.blitzar.bank_account_recipient.argumentprovider.validation;

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
                Arguments.of(Named.of("Empty String", StringUtils.EMPTY)),
                Arguments.of(Named.of("Space String", StringUtils.SPACE)),
                Arguments.of(Named.of("Null Value", (String) null))
        );
    }
}