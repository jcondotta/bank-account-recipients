package com.blitzar.bank_account_recipient.validation.security;

import com.blitzar.bank_account_recipient.validation.security.SQLInjectionPattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SQLInjectionPatternTest {

    private final SQLInjectionPattern sqlInjectionPattern = new SQLInjectionPattern();

    @Test
    public void shouldDetectBasicSQLInjection() {
        String maliciousInput = "SELECT * FROM users WHERE id = 1 OR 1=1 --";
        assertTrue(sqlInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldDetectUnionSQLInjection() {
        String maliciousInput = "UNION SELECT * FROM users";
        assertTrue(sqlInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldNotDetectSafeSQL() {
        String safeInput = "SELECT * FROM users WHERE id = 1";
        assertFalse(sqlInjectionPattern.containsPattern(safeInput));
    }
}
