package com.jcondotta.recipients.argument_provider.validation.security;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class XSSArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("XSS Script Tag", "<script>alert('XSS')</script>")),
                Arguments.of(Named.of("XSS Encoded <script>", "%3Cscript%3Ealert('XSS')%3C/script%3E"))
        );
    }
}