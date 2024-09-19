package com.blitzar.bank_account_recipient;

import io.micronaut.context.env.Environment;
import io.micronaut.context.env.PropertySource;
import io.micronaut.logging.impl.LogbackLoggingSystem;
import io.micronaut.logging.impl.LogbackUtils;
import io.micronaut.runtime.Micronaut;

import java.util.Map;

public class Application {

    public static void main(String[] args) {
        System.setProperty("logback.configurationFile", "logback-dev.xml");
        Micronaut.build(args)
                .mainClass(Application.class)
                .defaultEnvironments(Environment.DEVELOPMENT)
                .start();
    }
}