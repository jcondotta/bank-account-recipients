package com.blitzar.bank_account_recipient.argumentprovider.malicious;

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
                Arguments.of(Named.of("XSS Encoded <script>", "%3Cscript%3Ealert('XSS')%3C/script%3E")),
                Arguments.of(Named.of("XSS Image Tag", "<img src='x' onerror='alert(1)'>")),
                Arguments.of(Named.of("XSS Attribute Injection", "\" onclick=\"alert(1)\"")),
                Arguments.of(Named.of("XSS Event Handler", "onload=alert(1)")),
                Arguments.of(Named.of("XSS Inline JS", "javascript:alert('XSS')")),
                Arguments.of(Named.of("XSS in HTML Comments", "<!-- <script>alert('XSS')</script> -->")),
                Arguments.of(Named.of("XSS Encoded Characters in Image Tag", "%3Cimg%20src=x%20onerror=alert(1)%3E")),
                Arguments.of(Named.of("XSS in CSS", "background-image: url(javascript:alert('XSS'))"))
        );
    }
}