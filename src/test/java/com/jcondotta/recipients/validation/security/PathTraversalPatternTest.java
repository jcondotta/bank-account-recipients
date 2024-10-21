package com.jcondotta.recipients.validation.security;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathTraversalPatternTest {

    private final PathTraversalPattern pathTraversalPattern = new PathTraversalPattern();

    static Stream<Arguments> provideMaliciousInputs() {
        return Stream.of(
                Arguments.of("../../etc/passwd", true),
                Arguments.of("%2e%2e%2fetc/passwd", true),
                Arguments.of("%252e%252e%252fetc/passwd", true),
                Arguments.of("..\\windows\\system32", true),
                Arguments.of("%2e%2e%5cwindows%5csystem32", true),
                Arguments.of("%252e%252e%255cwindows%255csystem32", true),
                Arguments.of("/home/user/documents", false) // Safe input
        );
    }

    @ParameterizedTest
    @MethodSource("provideMaliciousInputs")
    void shouldDetectPathTraversal(String input, boolean expected) {
        if (expected) {
            assertTrue(pathTraversalPattern.containsPattern(input));
        }
        else {
            assertFalse(pathTraversalPattern.containsPattern(input));
        }
    }
}


