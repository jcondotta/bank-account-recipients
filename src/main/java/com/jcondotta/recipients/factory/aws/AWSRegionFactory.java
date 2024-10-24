package com.jcondotta.recipients.factory.aws;

import com.jcondotta.recipients.configuration.AwsConfiguration;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import software.amazon.awssdk.regions.Region;

@Factory
public class AWSRegionFactory {

    @Singleton
    @Requires(beans = AwsConfiguration.class)
    public Region region(AwsConfiguration awsConfiguration){
        return Region.of(awsConfiguration.region());
    }
}
