package com.blitzar.bank_account_recipient.factory.aws;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import software.amazon.awssdk.regions.Region;

@Factory
public class AWSRegionFactory {

    @Singleton
    @Requires(property = "aws.region")
    public Region region(@Value("${aws.region}") String region){
        return Region.of(region);
    }
}
