package com.blitzar.bank_account_recipient.security;

import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Singleton
public class TokenGeneratorService {

    public static final String DEFAULT_AUTH_USERNAME = AuthenticationProviderUserPassword.DEFAULT_AUTH;

    // Default token expiration in seconds (e.g., 1 hour = 3600 seconds)
    private static final int DEFAULT_TOKEN_EXPIRATION_IN_SECONDS = 3600;

    private final JwtTokenGenerator tokenGenerator;

    @Inject
    public TokenGeneratorService(JwtTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public String generateToken() {
        return generateToken(DEFAULT_AUTH_USERNAME, DEFAULT_TOKEN_EXPIRATION_IN_SECONDS, Map.of());
    }

    public String generateToken(String username) {
        return generateToken(username, DEFAULT_TOKEN_EXPIRATION_IN_SECONDS, Map.of());
    }

    public String generateToken(String username, int tokenExpirationInSeconds) {
        return generateToken(username, tokenExpirationInSeconds, Map.of());
    }

    public String generateToken(String username, Map<String, Object> additionalClaims) {
        return generateToken(username, DEFAULT_TOKEN_EXPIRATION_IN_SECONDS, additionalClaims);
    }

    public String generateToken(String username, int tokenExpirationInSeconds, Map<String, Object> additionalClaims) {
        Instant expirationTime = Instant.now().plusSeconds(tokenExpirationInSeconds);

        JWTClaimsSet.Builder jwtClaimBuilder = new JWTClaimsSet.Builder()
                .subject(username)
                .expirationTime(Date.from(expirationTime));

        additionalClaims.forEach(jwtClaimBuilder::claim);

        JWTClaimsSet jwtClaimsSet = jwtClaimBuilder.build();

        return tokenGenerator.generateToken(jwtClaimsSet.getClaims())
                .orElseThrow(() -> new RuntimeException("Failed to generate token"));
    }

    public String generateExpiredToken() {
        return generateToken(DEFAULT_AUTH_USERNAME, -3600, Map.of());
    }

    public String generateExpiredToken(String username) {
        return generateToken(username, -3600, Map.of());
    }
}
