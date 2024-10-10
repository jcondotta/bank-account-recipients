package com.blitzar.bank_account_recipient.factory.aws;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.SsmClientBuilder;

import java.net.URI;

@Factory
public class SSMClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(SSMClientFactory.class);

    @Singleton
    @Replaces(SsmClient.class)
    @Requires(property = "aws.ssm.endpoint", pattern = "^$")
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
    @Requires(property = "aws.ssm.endpoint", pattern = "(.|\\s)*\\S(.|\\s)*")
    public SsmClient ssmClientEndpointOverridden(AwsCredentials awsCredentials, Region region, @Value("${aws.ssm.endpoint}") String endpoint){

        logger.info("Building SSMClient with params: awsCredentials: {}, region: {} and endpoint: {}", awsCredentials, region, endpoint);

        return SsmClient.builder()
                .region(region)
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}