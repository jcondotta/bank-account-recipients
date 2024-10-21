package com.jcondotta.recipients.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;

import java.time.Clock;

@Factory
public class ClockFactory {

    @Prototype
    public Clock currentInstantUTC(){
        return Clock.systemUTC();
    }
}