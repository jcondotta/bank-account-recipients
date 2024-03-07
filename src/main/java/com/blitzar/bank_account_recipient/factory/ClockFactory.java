package com.blitzar.bank_account_recipient.factory;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.time.Clock;

@Factory
public class ClockFactory {

    @Singleton
    public Clock currentInstantUTC(){
        return Clock.systemUTC();
    }
}