package com.blitzar.bank_account_recipient.argumentprovider.malicious;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class CommandInjectionArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("Command Injection - RM", "; rm -rf /")),
                Arguments.of(Named.of("Command Injection - Listing Files", "&& ls")),
                Arguments.of(Named.of("Command Injection - Piping Output", "| cat /etc/passwd")),
                Arguments.of(Named.of("Command Injection - Redirect Output", "&& echo 'malicious' > /tmp/output")),
                Arguments.of(Named.of("Command Injection - Background Execution", "&& sleep 10 &")),
                Arguments.of(Named.of("Command Injection - Multiple Commands", "&& echo 'test'; ls"))
        );
    }
}