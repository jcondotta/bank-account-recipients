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
public final class AuthenticationProviderUserPassword<B> implements HttpRequestAuthenticationProvider<B> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationProviderUserPassword.class);

    public static final String DEFAULT_AUTH = "default";

    @Override
    public AuthenticationResponse authenticate(@Nullable HttpRequest<B> httpRequest,
                                               @NonNull AuthenticationRequest<String, String> authenticationRequest) {

        String identity = authenticationRequest.getIdentity();
        String secret = authenticationRequest.getSecret();

        if (DEFAULT_AUTH.equals(identity) && DEFAULT_AUTH.equals(secret)) {
            return AuthenticationResponse.success(identity);
        }
        else {
            LOGGER.warn("Authentication failed for user: {}", identity);
            return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }
    }
}
