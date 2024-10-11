package com.blitzar.bank_account_recipient.web.exception_handler;

import com.blitzar.bank_account_recipient.exception.RecipientNotFoundException;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Optional;

@Produces
@Singleton
@Requires(classes = { RecipientNotFoundException.class })
public class RecipientNotFoundExceptionHandler implements ExceptionHandler<RecipientNotFoundException, HttpResponse<?>> {

    private static final Logger logger = LoggerFactory.getLogger(RecipientNotFoundExceptionHandler.class);

    private final MessageSource messageSource;
    private final ErrorResponseProcessor<?> errorResponseProcessor;

    @Inject
    public RecipientNotFoundExceptionHandler(MessageSource messageSource, ErrorResponseProcessor<?> errorResponseProcessor) {
        this.messageSource = messageSource;
        this.errorResponseProcessor = errorResponseProcessor;
    }

    @Override
    @Status(value = HttpStatus.NOT_FOUND)
    public HttpResponse<?> handle(HttpRequest request, RecipientNotFoundException exception) {
        var errorMessage = messageSource.getMessage(exception.getMessage(), Locale.getDefault(), exception.getBankAccountId(), exception.getRecipientName())
                .orElse(exception.getMessage());

        logger.error(errorMessage);

        return errorResponseProcessor.processResponse(ErrorContext.builder(request)
                .cause(exception)
                .errorMessage(errorMessage)
                .build(), HttpResponse.notFound());
    }
}