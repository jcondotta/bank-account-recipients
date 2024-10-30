package com.jcondotta.recipients.validation.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PathTraversalPatternTest {

    private final PathTraversalPattern pathTraversalPattern = new PathTraversalPattern();

    @Test
    void shouldDetectConstraintViolation_whenInputContainsRelativePathTraversal() {
        String maliciousInput = "../../etc/passwd";
        Assertions.assertThat(pathTraversalPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect path traversal with relative path")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsEncodedPathTraversal() {
        String maliciousInput = "%2e%2e%2fetc/passwd";
        Assertions.assertThat(pathTraversalPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect encoded path traversal")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsDoubleEncodedPathTraversal() {
        String maliciousInput = "%252e%252e%252fetc/passwd";
        Assertions.assertThat(pathTraversalPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect double-encoded path traversal")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsWindowsPathTraversal() {
        String maliciousInput = "..\\windows\\system32";
        Assertions.assertThat(pathTraversalPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect Windows path traversal")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsEncodedWindowsPathTraversal() {
        String maliciousInput = "%2e%2e%5cwindows%5csystem32";
        Assertions.assertThat(pathTraversalPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect encoded Windows path traversal")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsDoubleEncodedWindowsPathTraversal() {
        String maliciousInput = "%252e%252e%255cwindows%255csystem32";
        Assertions.assertThat(pathTraversalPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect double-encoded Windows path traversal")
                .isTrue();
    }

    @Test
    void shouldNotDetectConstraintViolation_whenInputIsSafePath() {
        String safeInput = "/home/user/documents";
        Assertions.assertThat(pathTraversalPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect path traversal in safe path")
                .isFalse();
    }
}
