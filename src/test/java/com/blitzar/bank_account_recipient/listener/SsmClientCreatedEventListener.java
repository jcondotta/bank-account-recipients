package com.blitzar.bank_account_recipient.listener;

import com.blitzar.bank_account_recipient.configuration.ssm.JwtSignatureSecretConfiguration;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.ParameterType;

@Singleton
public class SsmClientCreatedEventListener implements BeanCreatedEventListener<SsmClient> {

    private static final Logger logger = LoggerFactory.getLogger(SsmClientCreatedEventListener.class);

    @Inject
    private JwtSignatureSecretConfiguration jwtSignatureSecretConfiguration;

    @Override
    public SsmClient onCreated(@NonNull BeanCreatedEvent<SsmClient> event) {
        var ssmClient = event.getBean();
        var jwtSignatureSecretName = jwtSignatureSecretConfiguration.name();
        logger.info("Putting parameter to SSM with name: {}", jwtSignatureSecretName);
        try {
            ssmClient.putParameter(builder -> builder.name(jwtSignatureSecretName)
                    .type(ParameterType.SECURE_STRING)
                    .overwrite(true)
                    .value("eyJhbGciOiJIUzI1NiJ9eyJzdWIiOiJkZWZhdW"));

            logger.info("Successfully put parameter: {}", jwtSignatureSecretName);
        }
        catch (Exception e) {
            logger.error("Failed to put parameter {}: {}", jwtSignatureSecretName, e.getMessage(), e);
        }

        return ssmClient;
    }
}
