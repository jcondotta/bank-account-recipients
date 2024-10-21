package com.jcondotta.recipients;

import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Bank account recipients API Specification",
                version = "1.0",
                contact = @Contact(name = "Jefferson Condotta", email = "jefferson.condotta@gmail.com", url = "https://jcondotta.io")
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