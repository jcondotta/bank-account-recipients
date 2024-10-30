package com.jcondotta.recipients.validation.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandInjectionPatternTest {

    private final CommandInjectionPattern commandInjectionPattern = new CommandInjectionPattern();

    @Test
    void shouldDetectCommandInjection_whenInputContainsRmAndLsCommands() {
        String maliciousInput = "rm -rf / && ls";
        Assertions.assertThat(commandInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect command injection with 'rm' and 'ls' commands")
                .isTrue();
    }

    @Test
    void shouldDetectCommandInjection_whenInputContainsShellExecution() {
        String maliciousInput = "sh -c 'ls -la'";
        Assertions.assertThat(commandInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect shell command injection with 'sh'")
                .isTrue();
    }

    @Test
    void shouldDetectCommandInjection_whenInputContainsPipingWithCat() {
        String maliciousInput = "cat /etc/passwd | grep root";
        Assertions.assertThat(commandInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect piping command injection with 'cat'")
                .isTrue();
    }

    @Test
    void shouldDetectCommandInjection_whenInputContainsOutputRedirection() {
        String maliciousInput = "echo 'data' > /tmp/output.txt";
        Assertions.assertThat(commandInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect output redirection command injection")
                .isTrue();
    }

    @Test
    void shouldDetectCommandInjection_whenInputContainsBackgroundExecution() {
        String maliciousInput = "sleep 10 &";
        Assertions.assertThat(commandInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect background execution command injection")
                .isTrue();
    }

    @Test
    void shouldNotDetectCommandInjection_whenInputIsSafe() {
        String safeInput = "echo 'Hello World'";
        Assertions.assertThat(commandInjectionPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect command injection in safe input")
                .isFalse();
    }

    @Test
    void shouldNotDetectCommandInjection_whenInputIsSimpleText() {
        String safeInput = "This is a safe string without command injection.";
        Assertions.assertThat(commandInjectionPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect command injection in simple text")
                .isFalse();
    }
}
