package com.blitzar.bank_account_recipient.argument_provider.validation.security;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class PathTraversalArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("Path Traversal - Unix", "../../etc/passwd")),
                Arguments.of(Named.of("Path Traversal - Encoded", "%2e%2e%2fetc/passwd")),
                Arguments.of(Named.of("Path Traversal - Windows", "..\\windows\\system32"))
        );
    }
}
