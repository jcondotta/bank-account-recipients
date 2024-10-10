package com.blitzar.bank_account_recipient.factory.aws;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.Parameter;

@Factory
public class SSMParameterFactory {

    private static final Logger logger = LoggerFactory.getLogger(SSMParameterFactory.class);

    @Singleton
    @Named("jwtSignatureSecret")
    public Parameter jwtSignatureSecretParameter(SsmClient ssmClient,
                             @Value("${aws.ssm.jwt-signature-secret.name}") String jwtSignatureSecretParameterName){

        logger.info("Fetching JWT signature secret from SSM parameter: {}", jwtSignatureSecretParameterName);

        var parameterResponse = ssmClient
                .getParameter(builder -> builder.name(jwtSignatureSecretParameterName)
                .withDecryption(true)
                .build());

        Parameter parameter = parameterResponse.parameter();

        logger.info("Successfully fetched JWT secret from SSM: {}", parameter.name());

        return parameterResponse.parameter();
    }
}