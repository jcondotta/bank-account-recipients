package com.blitzar.bank_account_recipient.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import jakarta.inject.Singleton;

import java.time.Clock;

@Factory
public class ClockFactory {

    @Prototype
    public Clock currentInstantUTC(){
        return Clock.systemUTC();
    }
}