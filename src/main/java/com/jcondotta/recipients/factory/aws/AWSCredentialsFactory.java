package com.jcondotta.recipients.factory.aws;

import com.jcondotta.recipients.configuration.AwsConfiguration;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;

@Factory
public class AWSCredentialsFactory {

    @Singleton
    @Requires(beans = AwsConfiguration.class)
    public AwsCredentials awsCredentials(AwsConfiguration awsConfiguration){
        return AwsBasicCredentials.create(awsConfiguration.accessKeyId(), awsConfiguration.secretKey());
    }
}
