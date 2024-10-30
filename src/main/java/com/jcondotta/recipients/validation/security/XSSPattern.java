package com.jcondotta.recipients.validation.security;

import java.util.regex.Pattern;

public class XSSPattern implements ThreatInputPattern {

    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(?i)<script[^>]*>.*?</script>" +   // Case-insensitive <script>...</script> pattern
            "|(%3Cscript%3E.*?%3C/script%3E)"   // Encoded <script>...</script> pattern
    );

    @Override
    public boolean containsPattern(String value) {
        return XSS_PATTERN.matcher(value).find();
    }
}