package com.jcondotta.recipients.validation.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class XSSPatternTest {

    private final XSSPattern xssPattern = new XSSPattern();

    @Test
    void shouldDetectConstraintViolation_whenInputContainsScriptTag() {
        String maliciousInput = "<script>alert('XSS');</script>";
        Assertions.assertThat(xssPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect XSS <script> tag")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsEncodedScriptTag() {
        String maliciousInput = "%3Cscript%3Ealert('XSS')%3C/script%3E";
        Assertions.assertThat(xssPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect encoded XSS <script> tag")
                .isTrue();
    }

    @Test
    void shouldNotDetectConstraintViolation_whenInputIsSafe() {
        String safeInput = "Jefferson";
        Assertions.assertThat(xssPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect XSS in safe name")
                .isFalse();
    }

    @Test
    void shouldNotDetectConstraintViolation_whenInputIsSafeWithNumbers() {
        String safeInputWithNumbers = "Jefferson2";
        Assertions.assertThat(xssPattern.containsPattern(safeInputWithNumbers))
                .withFailMessage("Expected not to detect XSS in safe name with numbers")
                .isFalse();
    }

    @Test
    void shouldNotDetectConstraintViolation_whenInputContainsValidSymbols() {
        String safeInputWithSymbols = "O'Connor-D'Angelo";
        Assertions.assertThat(xssPattern.containsPattern(safeInputWithSymbols))
                .withFailMessage("Expected not to detect XSS in safe name with valid symbols")
                .isFalse();
    }
}

