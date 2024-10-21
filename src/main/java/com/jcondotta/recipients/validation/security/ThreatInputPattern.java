package com.jcondotta.recipients.validation.security;

public interface ThreatInputPattern {

    boolean containsPattern(String value);

}