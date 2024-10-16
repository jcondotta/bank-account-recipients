package com.blitzar.bank_account_recipient.argument_provider.validation.security;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.Stream;

public class ThreatInputArgumentProvider implements ArgumentsProvider {

    private final List<ArgumentsProvider> argumentProviders = List.of(
            new XSSArgumentProvider(),
            new SQLInjectionArgumentProvider(),
            new CommandInjectionArgumentProvider(),
            new LDAPInjectionArgumentProvider(),
            new PathTraversalArgumentProvider()
    );

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return argumentProviders.stream()
                .flatMap(provider -> safeProvideArguments(provider, context));
    }

    private Stream<? extends Arguments> safeProvideArguments(ArgumentsProvider provider, ExtensionContext context) {
        try {
            return provider.provideArguments(context);
        }
        catch (Exception e) {
            throw new IllegalStateException("Error providing arguments for: " + provider.getClass().getSimpleName(), e);
        }
    }
}
