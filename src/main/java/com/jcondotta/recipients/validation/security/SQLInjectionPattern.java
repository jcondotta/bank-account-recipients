package com.jcondotta.recipients.validation.security;

import java.util.regex.Pattern;

public class SQLInjectionPattern implements ThreatInputPattern {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(\\bOR\\b\\s+\\d+\\s*=\\s*\\d+)|" +               // SQL OR condition like "OR 1=1"
            "(\\bOR\\b\\s+'.*?'\\s*=\\s*'.*?')|" +             // SQL OR condition with string values
            "(;.*?--)|" +                                      // SQL comment with semicolon
            "(--.*?$)|" +                                      // SQL comment
            "(\\bUNION\\b\\s+\\bSELECT\\b)|" +                 // SQL Union select
            "(WAITFOR\\s+DELAY)|" +                            // SQL Time delay attack
            "('.+--)",                                         // SQL comment with single quote
    Pattern.CASE_INSENSITIVE
    );


    @Override
    public boolean containsPattern(String value) {
        return SQL_INJECTION_PATTERN.matcher(value).find();
    }
}