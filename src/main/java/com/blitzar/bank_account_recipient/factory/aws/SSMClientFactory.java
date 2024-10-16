package com.blitzar.bank_account_recipient.factory.aws;

import com.blitzar.bank_account_recipient.configuration.ssm.SSMConfiguration.EndpointConfiguration;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.net.URI;

@Factory
public class SSMClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(SSMClientFactory.class);

    @Singleton
    @Replaces(SsmClient.class)
    @Requires(missing = EndpointConfiguration.class)
    public SsmClient ssmClient(Region region){
        var environmentVariableCredentialsProvider = EnvironmentVariableCredentialsProvider.create();
        var awsCredentials = environmentVariableCredentialsProvider.resolveCredentials();

        logger.info("Building SSMClient with params: awsCredentials: {}, region: {}", awsCredentials, region);

        return SsmClient.builder()
                .region(region)
                .credentialsProvider(environmentVariableCredentialsProvider)
                .build();
    }

    @Singleton
    @Replaces(SsmClient.class)
    @Requires(bean = EndpointConfiguration.class)
    public SsmClient ssmClientEndpointOverridden(AwsCredentials awsCredentials, Region region, EndpointConfiguration endpointConfiguration){
        logger.info("Building SSMClient with params: awsCredentials: {}, region: {} and endpoint: {}", awsCredentials, region, endpointConfiguration.endpoint());

        return SsmClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpointConfiguration.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}