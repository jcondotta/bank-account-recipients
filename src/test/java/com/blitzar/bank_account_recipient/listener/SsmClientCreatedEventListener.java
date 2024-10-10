package com.blitzar.bank_account_recipient.listener;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.ParameterType;
import software.amazon.awssdk.services.ssm.model.PutParameterRequest;

@Singleton
public class SsmClientCreatedEventListener implements BeanCreatedEventListener<SsmClient> {

    private static final Logger logger = LoggerFactory.getLogger(SsmClientCreatedEventListener.class);

    @Value("${aws.ssm.jwt-signature-secret.name}")
    private String jwtSignatureSecretParameterName;

    @Override
    public SsmClient onCreated(@NonNull BeanCreatedEvent<SsmClient> event) {
        var ssmClient = event.getBean();

        logger.info("Putting parameter to SSM with name: {}", jwtSignatureSecretParameterName);
        try {
            ssmClient.putParameter(builder -> builder.name(jwtSignatureSecretParameterName)
                    .type(ParameterType.SECURE_STRING)
                    .overwrite(true)
                    .value("eyJhbGciOiJIUzI1NiJ9eyJzdWIiOiJkZWZhdW"));

            logger.info("Successfully put parameter: {}", jwtSignatureSecretParameterName);
        }
        catch (Exception e) {
            logger.error("Failed to put parameter {}: {}", jwtSignatureSecretParameterName, e.getMessage(), e);
        }

        return ssmClient;
    }
}
