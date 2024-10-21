package com.jcondotta.recipients.validation.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LDAPInjectionPatternTest {

    private final LDAPInjectionPattern ldapInjectionPattern = new LDAPInjectionPattern();

    @Test
    void shouldDetectLDAPInjection() {
        String maliciousInput = "(&(uid=admin)(password=*))";
        assertTrue(ldapInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldDetectLDAPInjectionWithAdminAccess() {
        String maliciousInput = "admin)(|(uid=*))";
        assertTrue(ldapInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldNotDetectNonLDAPInjection() {
        String safeInput = "uid=user";
        assertFalse(ldapInjectionPattern.containsPattern(safeInput));
    }
}
