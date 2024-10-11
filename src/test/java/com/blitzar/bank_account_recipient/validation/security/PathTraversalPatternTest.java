package com.blitzar.bank_account_recipient.validation.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathTraversalPatternTest {

    private final PathTraversalPattern pathTraversalPattern = new PathTraversalPattern();

    @Test
    void shouldDetectPathTraversal() {
        String maliciousInput = "../../etc/passwd";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldDetectEncodedPathTraversal() {
        String maliciousInput = "%2e%2e%2fetc/passwd";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldDetectDoubleEncodedPathTraversal() {
        String maliciousInput = "%252e%252e%252fetc/passwd";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldDetectWindowsPathTraversal() {
        String maliciousInput = "..\\windows\\system32";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldNotDetectSafePath() {
        String safeInput = "/home/user/documents";
        assertFalse(pathTraversalPattern.containsPattern(safeInput));
    }

    @Test
    void shouldDetectEncodedWindowsPathTraversal() {
        String maliciousInput = "%2e%2e%5cwindows%5csystem32";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldDetectDoubleEncodedWindowsPathTraversal() {
        String maliciousInput = "%252e%252e%255cwindows%255csystem32";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }
}

