package com.jcondotta.recipients.listener;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.ParameterType;

@Singleton
public class SsmClientCreatedEventListener implements BeanCreatedEventListener<SsmClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SsmClientCreatedEventListener.class);

    @Value("aws.ssm.parameters.jwt-signature-secret")
    String jwtSignatureSecretParameterName;

    @Override
    public SsmClient onCreated(@NonNull BeanCreatedEvent<SsmClient> event) {
        var ssmClient = event.getBean();
        LOGGER.info("Putting parameter to SSM with name: {}", jwtSignatureSecretParameterName);
        try {
            ssmClient.putParameter(builder -> builder.name(jwtSignatureSecretParameterName)
                    .type(ParameterType.SECURE_STRING)
                    .overwrite(true)
                    .value("eyJhbGciOiJIUzI1NiJ9eyJzdWIiOiJkZWZhdW"));

            LOGGER.info("Successfully put parameter: {}", jwtSignatureSecretParameterName);
        }
        catch (Exception e) {
            LOGGER.error("Failed to put parameter {}: {}", jwtSignatureSecretParameterName, e.getMessage(), e);
        }

        return ssmClient;
    }
}
