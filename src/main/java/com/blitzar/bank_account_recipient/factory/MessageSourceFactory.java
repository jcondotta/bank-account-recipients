package com.blitzar.bank_account_recipient.factory;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Locale;

@Factory
public class MessageSourceFactory {

    @Singleton
    @Named("exceptionMessageSource")
    public MessageSource exceptionMessageSource() {
        return new ResourceBundleMessageSource("i18n/exceptions/exceptions", Locale.ENGLISH);
    }

    @Singleton
    public MessageSourceResolver exceptionMessageSourceResolver(@Named("exceptionMessageSource") MessageSource exceptionMessageSource) {
        return new MessageSourceResolver(exceptionMessageSource);
    }
}
