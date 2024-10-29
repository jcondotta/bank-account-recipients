package com.jcondotta.recipients.validation.security;

import java.util.regex.Pattern;

public class XSSPattern implements ThreatInputPattern {

    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(<script[^>]*+>[^<]*+</script[^>]*+>)|" +       // XSS <script> tag
            "(%3C.*?script.*?%3E)|" +                        // Encoded <script>
            "(<img.*?onerror.*?>)|" +                        // XSS Image tag
            "(\".*?on.*?=.*?\")|" +                          // XSS Attribute injection
            "(javascript:.*?alert\\(.*?\\))|" +              // Inline JS execution
            "(onload=alert\\([^)]*+\\))|" +                     // XSS event handler
            "(<!--.*?<script.*?>.*?</script.*?>.*?-->)|" +   // XSS in HTML comments
            "(%3C.*?img.*?onerror.*?%3E)|" +                 // Encoded XSS in image tag
            "(background-image:\\s*+url\\([^)]*+javascript:[^)]*+\\))" // XSS in CSS
    );

    @Override
    public boolean containsPattern(String value) {
        return XSS_PATTERN.matcher(value).find();
    }
}