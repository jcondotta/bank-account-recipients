package com.jcondotta.recipients.validation.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class ThreatInputPatternDetectorTest {

    private final ThreatInputPatternDetector detector = new ThreatInputPatternDetector();

    static Stream<Arguments> provideMaliciousInputs() {
        return Stream.of(
                Arguments.of("SELECT * FROM users WHERE id = 1 OR 1=1 --", "SQL Injection"),     // SQL Injection
                Arguments.of("rm -rf / && ls", "Command Injection"),                             // Command Injection
                Arguments.of("../../etc/passwd", "Path Traversal"),                              // Path Traversal
                Arguments.of("<script>alert('XSS');</script>", "XSS Attack"),                    // XSS Attack
                Arguments.of("(&(uid=admin)(password=*))", "LDAP Injection")                     // LDAP Injection
        );
    }

    @ParameterizedTest(name = "shouldDetectThreatsInDetector_whenInputContains{1}")
    @MethodSource("provideMaliciousInputs")
    void shouldDetectThreatsInDetector_whenInputContainsThreat(String maliciousInput, String threatType) {
        Assertions.assertThat(detector.containsAnyPattern(maliciousInput))
                .withFailMessage("Expected to detect %s in input: \"%s\"", threatType, maliciousInput)
                .isTrue();
    }

    @Test
    void shouldDetectCustomPattern_whenCustomPatternIsProvided() {
        ThreatInputPattern customPattern = value -> value.contains("custom");
        ThreatInputPatternDetector customDetector = new ThreatInputPatternDetector(List.of(customPattern));

        Assertions.assertThat(customDetector.containsAnyPattern("this is a custom input"))
                .withFailMessage("Expected to detect custom pattern in input")
                .isTrue();
        Assertions.assertThat(customDetector.containsAnyPattern("this is safe"))
                .withFailMessage("Expected not to detect any pattern in safe input")
                .isFalse();
    }
}
