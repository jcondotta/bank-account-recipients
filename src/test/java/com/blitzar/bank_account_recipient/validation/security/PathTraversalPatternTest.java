package com.blitzar.bank_account_recipient.validation.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathTraversalPatternTest {

    private final PathTraversalPattern pathTraversalPattern = new PathTraversalPattern();

    @Test
    public void shouldDetectPathTraversal() {
        String maliciousInput = "../../etc/passwd";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldDetectEncodedPathTraversal() {
        String maliciousInput = "%2e%2e%2fetc/passwd";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldDetectDoubleEncodedPathTraversal() {
        String maliciousInput = "%252e%252e%252fetc/passwd";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldDetectWindowsPathTraversal() {
        String maliciousInput = "..\\windows\\system32";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldNotDetectSafePath() {
        String safeInput = "/home/user/documents";
        assertFalse(pathTraversalPattern.containsPattern(safeInput));
    }

    @Test
    public void shouldDetectEncodedWindowsPathTraversal() {
        String maliciousInput = "%2e%2e%5cwindows%5csystem32";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldDetectDoubleEncodedWindowsPathTraversal() {
        String maliciousInput = "%252e%252e%255cwindows%255csystem32";
        assertTrue(pathTraversalPattern.containsPattern(maliciousInput));
    }
}

