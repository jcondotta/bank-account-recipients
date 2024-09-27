package com.blitzar.bank_account_recipient.argumentprovider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class MaliciousInputArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.concat(
                new XSSArgumentProvider().provideArguments(context),
                Stream.concat(
                        new SQLInjectionArgumentProvider().provideArguments(context),
                        new CommandInjectionArgumentProvider().provideArguments(context)
                )
        );
    }
}