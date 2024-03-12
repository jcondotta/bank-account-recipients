package com.blitzar.bank_account_recipient.factory.aws;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Factory
public class AWSCredentialsFactory {

    @Value("${aws.access-key-id}")
    protected String accessKey;

    @Value("${aws.secret-key}")
    protected String secretKey;

    @Singleton
    public AwsCredentials awsCredentials(){
        return AwsBasicCredentials.create(accessKey, secretKey);
    }

    @Singleton
    public AwsCredentialsProvider awsCredentialsProvider(AwsCredentials awsCredentials){
        return StaticCredentialsProvider.create(awsCredentials);
    }
}
