package com.blitzar.bank_account_recipient.validation.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreatInputPatternDetectorTest {

    private final ThreatInputPatternDetector detector = new ThreatInputPatternDetector();

    static Stream<Arguments> provideMaliciousInputs() {
        return Stream.of(
                Arguments.of("<script>alert('XSS')</script>"), // XSS attack
                Arguments.of("SELECT * FROM users WHERE id = 1 OR 1=1 --"), // SQL Injection
                Arguments.of("rm -rf / && ls") // Command Injection
        );
    }

    @ParameterizedTest
    @MethodSource("provideMaliciousInputs")
    void shouldDetectThreatsInDetector(String maliciousInput) {
        assertTrue(detector.containsAnyPattern(maliciousInput));
    }

    @Test
    void shouldUseCustomPatterns() {
        ThreatInputPattern customPattern = value -> value.contains("custom");
        ThreatInputPatternDetector customDetector = new ThreatInputPatternDetector(List.of(customPattern));

        assertTrue(customDetector.containsAnyPattern("this is a custom input"));
        assertFalse(customDetector.containsAnyPattern("this is safe"));
    }
}
