package com.blitzar.bank_account_recipient.argumentprovider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BlankAndNonPrintableCharactersArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.concat(
                new BlankValuesArgumentProvider().provideArguments(context),
                new NonPrintableCharactersArgumentProvider().provideArguments(context)
        );
    }
}