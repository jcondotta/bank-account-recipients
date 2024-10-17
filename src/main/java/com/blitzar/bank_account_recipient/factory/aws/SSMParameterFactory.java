package com.blitzar.bank_account_recipient.factory.aws;

import com.blitzar.bank_account_recipient.configuration.ssm.JwtSignatureSecretConfiguration;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.Parameter;

@Factory
public class SSMParameterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSMParameterFactory.class);

    @Singleton
    @Named("jwtSignatureSecret")
    @Requires(bean = JwtSignatureSecretConfiguration.class)
    public Parameter jwtSignatureSecretParameter(SsmClient ssmClient, JwtSignatureSecretConfiguration jwtSignatureSecretConfiguration){

        LOGGER.info("Fetching JWT signature secret from SSM parameter: {}", jwtSignatureSecretConfiguration.name());

        var parameterResponse = ssmClient
                .getParameter(builder -> builder.name(jwtSignatureSecretConfiguration.name())
                .withDecryption(true)
                .build());

        Parameter parameter = parameterResponse.parameter();

        LOGGER.debug("Successfully fetched JWT secret from SSM: {}", parameter.name());

        return parameterResponse.parameter();
    }
}