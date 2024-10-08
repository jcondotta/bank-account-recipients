package com.blitzar.bank_account_recipient.security;

import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class AuthenticationService {

    public static final String LOGIN_ENDPOINT_PATH = "/login";


    private RequestSpecification requestSpecification;

    @Inject
    public AuthenticationService(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public AuthenticationResponseDTO authenticate(UsernamePasswordCredentials usernamePasswordCredentials) {
        var authenticationResponseDTO = RestAssured.given()
            .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(usernamePasswordCredentials)
        .when()
            .post(LOGIN_ENDPOINT_PATH)
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                    .as(AuthenticationResponseDTO.class);

        return authenticationResponseDTO;
    }

    public AuthenticationResponseDTO authenticate(String identity, String secret) {
        var usernamePasswordCredentials = new UsernamePasswordCredentials(identity, secret);
        return authenticate(usernamePasswordCredentials);
    }

    public AuthenticationResponseDTO authenticate() {
        String username = AuthenticationProviderUserPassword.DEFAULT_AUTH_USERNAME;
        String password = AuthenticationProviderUserPassword.DEFAULT_AUTH_PASSWORD;

        return authenticate(username, password);
    }
}