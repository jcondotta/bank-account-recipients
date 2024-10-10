package com.blitzar.bank_account_recipient.validation.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LDAPInjectionPatternTest {

    private final LDAPInjectionPattern ldapInjectionPattern = new LDAPInjectionPattern();

    @Test
    public void shouldDetectLDAPInjection() {
        String maliciousInput = "(&(uid=admin)(password=*))";
        assertTrue(ldapInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldDetectLDAPInjectionWithAdminAccess() {
        String maliciousInput = "admin)(|(uid=*))";
        assertTrue(ldapInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldNotDetectNonLDAPInjection() {
        String safeInput = "uid=user";
        assertFalse(ldapInjectionPattern.containsPattern(safeInput));
    }
}
