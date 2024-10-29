package com.jcondotta.recipients.factory.aws;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
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
    @Requires(property = "aws.ssm.parameters.jwt-signature-secret")
    public Parameter jwtSignatureSecretParameter(SsmClient ssmClient, @Value("aws.ssm.parameters.jwt-signature-secret") String jwtSignatureSecretParameterName){

        LOGGER.info("Fetching JWT signature secret from SSM parameter: {}", jwtSignatureSecretParameterName);

        var parameterResponse = ssmClient
                .getParameter(builder -> builder.name(jwtSignatureSecretParameterName)
                .withDecryption(true)
                .build());

        Parameter parameter = parameterResponse.parameter();

        LOGGER.debug("Successfully fetched JWT secret from SSM: {}", parameter.name());

        return parameterResponse.parameter();
    }
}