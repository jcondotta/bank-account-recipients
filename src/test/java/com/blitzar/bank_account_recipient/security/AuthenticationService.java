package com.blitzar.bank_account_recipient.security;

import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static io.restassured.RestAssured.given;

@Singleton
public class AuthenticationService {

    public static final String LOGIN_ENDPOINT_PATH = "/login";

    private RequestSpecification requestSpecification;

    @Inject
    public AuthenticationService(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public AuthenticationResponseDTO authenticate(UsernamePasswordCredentials usernamePasswordCredentials) {
        return given()
            .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(usernamePasswordCredentials)
        .when()
            .post(LOGIN_ENDPOINT_PATH)
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                    .as(AuthenticationResponseDTO.class);
    }

    public AuthenticationResponseDTO authenticate(String identity, String secret) {
        var usernamePasswordCredentials = new UsernamePasswordCredentials(identity, secret);
        return authenticate(usernamePasswordCredentials);
    }

    public AuthenticationResponseDTO authenticate() {
        String username = AuthenticationProviderUserPassword.DEFAULT_AUTH;
        String password = AuthenticationProviderUserPassword.DEFAULT_AUTH;

        return authenticate(username, password);
    }
}