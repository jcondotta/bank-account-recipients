package com.blitzar.bank_account_recipient.argumentprovider.validation.security;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class LDAPInjectionArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("LDAP Injection - Admin Access", "(&(uid=admin)(password=*))")),
                Arguments.of(Named.of("LDAP Injection - Special Characters", "admin)(|(uid=*))"))
        );
    }
}
