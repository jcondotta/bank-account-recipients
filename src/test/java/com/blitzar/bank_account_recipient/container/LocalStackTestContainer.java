package com.blitzar.bank_account_recipient.container;

import io.micronaut.test.support.TestPropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Testcontainers
public interface LocalStackTestContainer extends TestPropertyProvider {

    Logger logger = LoggerFactory.getLogger(LocalStackTestContainer.class);

    String LOCAL_STACK_IMAGE_NAME = "localstack/localstack:3.7.0";
    DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(LOCAL_STACK_IMAGE_NAME);

    String JWT_SIGNATURE_SECRET_NAME = "/jwt/signature/test/secret";
    String DYNAMODB_RECIPIENTS_TABLE_NAME = "recipients-test";

    LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(Service.DYNAMODB, Service.SSM)
            .withLogConsumer(outputFrame -> logger.info(outputFrame.getUtf8StringWithoutLineEnding()));

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
                "AWS_DYNAMODB_RECIPIENTS_TABLE_NAME", DYNAMODB_RECIPIENTS_TABLE_NAME,
                "AWS_SSM_ENDPOINT", LOCALSTACK_CONTAINER.getEndpointOverride(Service.SSM).toString(),
                "JWT_SIGNATURE_SECRET_NAME", JWT_SIGNATURE_SECRET_NAME);
    }

    default void logContainerConfiguration() {
        StringBuilder sbConfig = new StringBuilder();
        sbConfig.append("LocalStack container configuration:\n")
                .append(String.format("  Access Key: %s%n", LOCALSTACK_CONTAINER.getAccessKey()))
                .append(String.format("  Secret Key: %s%n", LOCALSTACK_CONTAINER.getSecretKey()))
                .append(String.format("  Region: %s%n", LOCALSTACK_CONTAINER.getRegion()))
                .append(String.format("  DynamoDB Endpoint: %s%n", LOCALSTACK_CONTAINER.getEndpointOverride(Service.DYNAMODB)))
                .append(String.format("  DynamoDB Recipients table name: %s%n", DYNAMODB_RECIPIENTS_TABLE_NAME))
                .append(String.format("  SSM Endpoint: %s%n", LOCALSTACK_CONTAINER.getEndpointOverride(Service.SSM)))
                .append(String.format("  SSM JWT Signature Secret name: %s%n", JWT_SIGNATURE_SECRET_NAME));

        logger.info(sbConfig.toString());
    }
}