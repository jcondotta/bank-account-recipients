package com.jcondotta.recipients.validation.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLInjectionPatternTest {

    private final SQLInjectionPattern sqlInjectionPattern = new SQLInjectionPattern();

    @Test
    void shouldDetectBasicSQLInjection() {
        String maliciousInput = "SELECT * FROM users WHERE id = 1 OR 1=1 --";
        assertTrue(sqlInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldDetectUnionSQLInjection() {
        String maliciousInput = "UNION SELECT * FROM users";
        assertTrue(sqlInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldNotDetectSafeSQL() {
        String safeInput = "SELECT * FROM users WHERE id = 1";
        assertFalse(sqlInjectionPattern.containsPattern(safeInput));
    }
}
