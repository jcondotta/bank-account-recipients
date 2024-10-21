package com.jcondotta.recipients;

import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "${api.title}",
                version = "${api.version}",
                description = "${api.description}",
                contact = @Contact(name = "${api.contact.recipientName}", email = "${api.contact.email}", url = "${api.contact.url}")
        ), servers = @Server(url = "http://localhost:8086")
)
public class Application {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(Application.class)
                .defaultEnvironments(Environment.DEVELOPMENT)
                .start();
    }
}