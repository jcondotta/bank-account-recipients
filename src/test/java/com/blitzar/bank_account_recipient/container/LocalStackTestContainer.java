package com.blitzar.bank_account_recipient.container;

import io.micronaut.test.support.TestPropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.ParameterType;
import software.amazon.awssdk.services.ssm.model.PutParameterRequest;

import java.util.Map;

@Testcontainers
public interface LocalStackTestContainer extends TestPropertyProvider {

    Logger logger = LoggerFactory.getLogger(LocalStackTestContainer.class);

    String localStackImageName = "localstack/localstack:3.7.0";
    DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(localStackImageName);

    LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(Service.DYNAMODB, Service.SSM)
            .withLogConsumer(outputFrame -> logger.debug(outputFrame.getUtf8StringWithoutLineEnding()));

    @Override
    default Map<String, String> getProperties() {
        try {
            Startables.deepStart(LOCALSTACK_CONTAINER).join();
        }
        catch (Exception e) {
            logger.error("Failed to start LocalStack container: " + e.getMessage());

            throw new RuntimeException("Failed to start LocalStack container", e);
        }

        logContainerConfiguration();

        return getAWSProperties();
    }

    default Map<String, String> getAWSProperties() {
        return Map.of(
                "AWS_ACCESS_KEY_ID", LOCALSTACK_CONTAINER.getAccessKey(),
                "AWS_SECRET_ACCESS_KEY", LOCALSTACK_CONTAINER.getSecretKey(),
                "AWS_DEFAULT_REGION", LOCALSTACK_CONTAINER.getRegion(),
                "AWS_DYNAMODB_ENDPOINT", LOCALSTACK_CONTAINER.getEndpointOverride(Service.DYNAMODB).toString(),
                "AWS_SSM_ENDPOINT", LOCALSTACK_CONTAINER.getEndpointOverride(Service.SSM).toString());
    }

    default void logContainerConfiguration() {
        StringBuilder sbConfig = new StringBuilder();
        sbConfig.append("LocalStack container configuration:\n")
                .append(String.format("  Access Key: %s%n", LOCALSTACK_CONTAINER.getAccessKey()))
                .append(String.format("  Secret Key: %s%n", LOCALSTACK_CONTAINER.getSecretKey()))
                .append(String.format("  Region: %s%n", LOCALSTACK_CONTAINER.getRegion()))
                .append(String.format("  DynamoDB Endpoint: %s%n", LOCALSTACK_CONTAINER.getEndpointOverride(Service.DYNAMODB)))
                .append(String.format("  SSM Endpoint: %s%n", LOCALSTACK_CONTAINER.getEndpointOverride(Service.SSM)));

        logger.info(sbConfig.toString());
    }
}


