package com.blitzar.bank_account_recipient.argumentprovider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BlankAndNonPrintableCharactersArgumentProvider implements ArgumentsProvider {

    private final BlankValuesArgumentProvider blankValuesArgumentProvider;
    private final NonPrintableCharactersArgumentProvider nonPrintableCharactersArgumentProvider;

    public BlankAndNonPrintableCharactersArgumentProvider() {
        blankValuesArgumentProvider = new BlankValuesArgumentProvider();
        nonPrintableCharactersArgumentProvider = new NonPrintableCharactersArgumentProvider();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.concat(
                blankValuesArgumentProvider.provideArguments(context),
                nonPrintableCharactersArgumentProvider.provideArguments(context)
        );
    }
}