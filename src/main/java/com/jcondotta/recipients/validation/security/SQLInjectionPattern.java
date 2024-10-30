package com.jcondotta.recipients.validation.security;

import java.util.regex.Pattern;

public class SQLInjectionPattern implements ThreatInputPattern {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(\\bOR\\b\\s+\\d+\\s*=\\s*\\d+)" +                 // SQL OR condition with numeric comparison
            "|(\\bOR\\b\\s+'[^']*'\\s*=\\s*'[^']*')" +          // SQL OR condition with string comparison
            "|(;\\s*--)" +                                      // SQL comment with semicolon
            "|(--\\s*$)" +                                      // SQL comment at line end
            "|(\\bUNION\\b\\s+\\bSELECT\\b)" +                  // SQL UNION SELECT
            "|(WAITFOR\\s+DELAY)" +                             // SQL delay attack
            "|('.*--)" +                                        // SQL comment with single quote
            "|(\\bSLEEP\\b\\s*\\(\\s*\\d+\\s*\\))",             // SQL sleep function
            Pattern.CASE_INSENSITIVE
    );


    @Override
    public boolean containsPattern(String value) {
        return SQL_INJECTION_PATTERN.matcher(value).find();
    }
}