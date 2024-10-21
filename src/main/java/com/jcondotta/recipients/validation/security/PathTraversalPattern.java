package com.jcondotta.recipients.validation.security;

import java.util.regex.Pattern;

public class PathTraversalPattern implements ThreatInputPattern {

    // Updated regex to detect Unix, Windows, encoded, and double-encoded path traversal
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
            "(\\.\\./|\\.\\.\\\\)" +  // Detect Unix or Windows-style path traversal
            "|(%2e%2e%2f|%2e%2e%5c)" + // Encoded path traversal: %2e%2e/
            "|(%252e%252e%252f|%252e%252e%255c)", // Double-encoded path traversal: %252e%252e/
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean containsPattern(String value) {
        return PATH_TRAVERSAL_PATTERN.matcher(value).find();
    }
}