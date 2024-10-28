package com.jcondotta.recipients.web.exception_handler;

import com.jcondotta.recipients.exception.RecipientAlreadyExistsException;
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

@Produces
@Singleton
@Requires(classes = { RecipientAlreadyExistsException.class })
public class RecipientAlreadyExistsExceptionHandler implements ExceptionHandler<RecipientAlreadyExistsException, HttpResponse<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientAlreadyExistsExceptionHandler.class);

    private final MessageSource messageSource;
    private final ErrorResponseProcessor<?> errorResponseProcessor;

    @Inject
    public RecipientAlreadyExistsExceptionHandler(MessageSource messageSource, ErrorResponseProcessor<?> errorResponseProcessor) {
        this.messageSource = messageSource;
        this.errorResponseProcessor = errorResponseProcessor;
    }

    @Override
    @Status(value = HttpStatus.CONFLICT)
    public HttpResponse<?> handle(HttpRequest request, RecipientAlreadyExistsException exception) {
        var errorMessage = messageSource.getMessage(exception.getMessage(), Locale.getDefault(), exception.getBankAccountId(), exception.getRecipientName())
                .orElse(exception.getMessage());

        LOGGER.error(errorMessage);

        return errorResponseProcessor.processResponse(ErrorContext.builder(request)
                .cause(exception)
                .errorMessage(errorMessage)
                .build(), HttpResponse.status(HttpStatus.CONFLICT));
    }
}