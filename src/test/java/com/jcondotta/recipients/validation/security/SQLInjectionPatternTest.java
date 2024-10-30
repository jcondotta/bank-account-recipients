package com.jcondotta.recipients.validation.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SQLInjectionPatternTest {

    private final SQLInjectionPattern sqlInjectionPattern = new SQLInjectionPattern();

    @Test
    void shouldDetectConstraintViolation_whenInputContainsORCondition() {
        String maliciousInput = "SELECT * FROM users WHERE id = 1 OR 1=1 --";
        Assertions.assertThat(sqlInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect SQL injection OR condition")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsUnionSelect() {
        String maliciousInput = "UNION SELECT * FROM users";
        Assertions.assertThat(sqlInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect SQL injection UNION SELECT")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsSQLDelay() {
        String maliciousInput = "WAITFOR DELAY '00:00:05'";
        Assertions.assertThat(sqlInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect SQL injection with WAITFOR DELAY")
                .isTrue();
    }

    @Test
    void shouldDetectConstraintViolation_whenInputContainsSleepFunction() {
        String maliciousInput = "SLEEP(5)";
        Assertions.assertThat(sqlInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect SQL injection with SLEEP function")
                .isTrue();
    }

    @Test
    void shouldNotDetectConstraintViolation_whenInputIsSafeSQL() {
        String safeInput = "SELECT * FROM users WHERE id = 1";
        Assertions.assertThat(sqlInjectionPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect SQL injection in safe SQL")
                .isFalse();
    }
}
