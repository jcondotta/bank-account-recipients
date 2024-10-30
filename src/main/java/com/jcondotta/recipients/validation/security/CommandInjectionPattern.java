package com.jcondotta.recipients.validation.security;

import java.util.regex.Pattern;

public class CommandInjectionPattern implements ThreatInputPattern {

    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
            "(;\\s*rm\\b)|" +                                  // Semicolon followed by 'rm' command
            "(&&\\s*ls\\b)|" +                                 // Double ampersand followed by 'ls' command
            "(\\|\\s*cat\\b)|" +                               // Pipe followed by 'cat' command
            "(>\\s*[^&>]+)|" +                                 // Output redirection (with '>' symbol)
            "(&\\s*$)|" +                                      // Background execution with '&' at end
            "(;\\s*ls\\b)|" +                                  // Semicolon followed by 'ls' command
            "(\\|\\|\\s*true\\b)|" +                           // Double pipe with 'true' bypass
            "(\\bsh\\b|\\bchmod\\b|\\bchown\\b|\\bcat\\b)",    // Common shell commands including 'cat'
            Pattern.CASE_INSENSITIVE
    );


    @Override
    public boolean containsPattern(String value) {
        return COMMAND_INJECTION_PATTERN.matcher(value).find();
    }
}
