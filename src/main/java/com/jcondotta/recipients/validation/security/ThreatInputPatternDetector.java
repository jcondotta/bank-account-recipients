package com.jcondotta.recipients.validation.security;

import java.util.List;

public class ThreatInputPatternDetector {

    private final List<ThreatInputPattern> patterns;

    public ThreatInputPatternDetector() {
        this.patterns = List.of(
            new XSSPattern(),
            new SQLInjectionPattern(),
            new CommandInjectionPattern(),
            new PathTraversalPattern(),
            new LDAPInjectionPattern()
        );
    }

    public ThreatInputPatternDetector(List<ThreatInputPattern> patterns) {
        this.patterns = patterns;
    }

    public boolean containsAnyPattern(String value) {
        return patterns.stream().anyMatch(pattern -> pattern.containsPattern(value));
    }
}