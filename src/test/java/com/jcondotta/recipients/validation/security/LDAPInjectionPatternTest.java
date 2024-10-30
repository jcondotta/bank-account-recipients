package com.jcondotta.recipients.validation.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LDAPInjectionPatternTest {

    private final LDAPInjectionPattern ldapInjectionPattern = new LDAPInjectionPattern();

    @Test
    void shouldDetectLDAPInjection_whenInputContainsWildcardPassword() {
        String maliciousInput = "(&(uid=admin)(password=*))";
        Assertions.assertThat(ldapInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect LDAP injection with wildcard password")
                .isTrue();
    }

    @Test
    void shouldDetectLDAPInjection_whenInputContainsORCondition() {
        String maliciousInput = "admin)(|(uid=*))";
        Assertions.assertThat(ldapInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect LDAP injection with OR condition")
                .isTrue();
    }

    @Test
    void shouldDetectLDAPInjection_whenInputContainsANDCondition() {
        String maliciousInput = "(&(objectClass=*)(uid=*))";
        Assertions.assertThat(ldapInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect LDAP injection with AND condition")
                .isTrue();
    }

    @Test
    void shouldNotDetectLDAPInjection_whenInputIsSafe() {
        String safeInput = "uid=user";
        Assertions.assertThat(ldapInjectionPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect LDAP injection in safe input")
                .isFalse();
    }

    @Test
    void shouldNotDetectLDAPInjection_whenInputContainsSimpleFilter() {
        String safeInput = "(cn=John Doe)";
        Assertions.assertThat(ldapInjectionPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect LDAP injection in a simple filter")
                .isFalse();
    }
}
