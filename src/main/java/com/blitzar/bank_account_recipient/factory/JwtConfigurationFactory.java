package com.blitzar.bank_account_recipient.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.model.Parameter;

@Factory
public class JwtConfigurationFactory {

    private static final Logger logger = LoggerFactory.getLogger(JwtConfigurationFactory.class);

    @Singleton
    @Replaces(SecretSignatureConfiguration.class)
    public SecretSignatureConfiguration secretSignatureConfiguration(@Named("jwtSignatureSecret") final Parameter jwtSignatureSecretParameter) {
        final String jwtSignatureSecret = jwtSignatureSecretParameter.value();

        if (StringUtils.isBlank(jwtSignatureSecret)) {
            throw new IllegalArgumentException("JWT signature secret must not be null or empty");
        }

        String maskedSecret = StringUtils.left(jwtSignatureSecret, 4) + "***************";
        logger.info("Configuring JWT secret from SSM Parameter Store: {}", maskedSecret);

        // Create and configure the SecretSignatureConfiguration
        SecretSignatureConfiguration secretSignatureConfiguration = new SecretSignatureConfiguration("JWT Signature Secret Configuration");
        secretSignatureConfiguration.setSecret(jwtSignatureSecret);

        return secretSignatureConfiguration;
    }
}
