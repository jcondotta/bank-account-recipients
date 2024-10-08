package com.blitzar.bank_account_recipient.factory;

import io.micronaut.context.MessageSource;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Locale;

public class MessageSourceResolver {

    private final MessageSource messageSource;

    public MessageSourceResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key) {
        return getMessage(key, Locale.getDefault());
    }

    public String getMessage(String key, Object... args) {
        return getMessage(key, Locale.getDefault(), args);
    }

    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, locale).orElseThrow(() -> new IllegalArgumentException("Message not found for key: " + key));
    }

    public String getMessage(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, locale, args).orElseThrow(() -> new IllegalArgumentException("Message not found for key: " + key));
    }
}