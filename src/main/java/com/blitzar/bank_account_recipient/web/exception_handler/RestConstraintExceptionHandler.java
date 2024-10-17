package com.blitzar.bank_account_recipient.web.exception_handler;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Produces
@Singleton
@Replaces(value = ConstraintExceptionHandler.class)
@Requires(classes = { ConstraintViolationException.class, ExceptionHandler.class })
public class RestConstraintExceptionHandler extends ConstraintExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestConstraintExceptionHandler.class);
    private final MessageSource messageSource;
    private final ErrorResponseProcessor<?> errorResponseProcessor;

    public RestConstraintExceptionHandler(MessageSource messageSource, ErrorResponseProcessor<?> errorResponseProcessor) {
        super(errorResponseProcessor);
        this.messageSource = messageSource;
        this.errorResponseProcessor = errorResponseProcessor;
    }

    @Override
    @Status(value = HttpStatus.BAD_REQUEST)
    public HttpResponse<?> handle(HttpRequest request, ConstraintViolationException exception) {
        var locale = Locale.getDefault();

        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String message = violation.getMessage();

            String localizedMessage = messageSource.getMessage(message, locale).orElse(message);
            errorMessages.add(localizedMessage);
            LOGGER.error(localizedMessage);
        }

        var responseBody = Map.of(
                "_embedded", Map.of("errors", errorMessages.stream()
                        .map(msg -> Map.of("message", msg))
                        .toList()), // Convert messages to the desired format
                "message", "Bad Request" // Include the standard message
        );

        return errorResponseProcessor.processResponse(
                ErrorContext.builder(request)
                        .cause(exception)
                        .errorMessages(errorMessages)
                        .build(),
                HttpResponse.badRequest().body(responseBody)
        );
    }
}