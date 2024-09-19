package com.blitzar.bank_account_recipient;

import io.micronaut.context.env.Environment;
import io.micronaut.context.env.PropertySource;
import io.micronaut.logging.impl.LogbackLoggingSystem;
import io.micronaut.logging.impl.LogbackUtils;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

import java.util.Map;

@OpenAPIDefinition(
        info = @Info(
                title = "bank-account-recipients-api",
                version = "1.0"
        ), servers = @Server(url = "http://localhost:8086")
)
public class Application {

    public static void main(String[] args) {
        System.setProperty("logback.configurationFile", "logback-dev.xml");
        Micronaut.build(args)
                .mainClass(Application.class)
                .defaultEnvironments(Environment.DEVELOPMENT)
                .start();
    }
}