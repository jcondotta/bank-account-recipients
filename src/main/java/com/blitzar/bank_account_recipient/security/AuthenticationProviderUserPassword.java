package com.blitzar.bank_account_recipient.security;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthenticationProviderUserPassword<B> implements HttpRequestAuthenticationProvider<B> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationProviderUserPassword.class);

    public static final String DEFAULT_AUTH_USERNAME = "default";
    public static final String DEFAULT_AUTH_PASSWORD = "default";

    @Override
    public AuthenticationResponse authenticate(@Nullable HttpRequest<B> httpRequest,
                                               @NonNull AuthenticationRequest<String, String> authenticationRequest) {

        String username = authenticationRequest.getIdentity();
        String password = authenticationRequest.getSecret();

        if (DEFAULT_AUTH_USERNAME.equals(username) && DEFAULT_AUTH_PASSWORD.equals(password)) {
            return AuthenticationResponse.success(username);
        }
        else {
            logger.warn("Authentication failed for user: {}", username);
            return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }
    }
}
