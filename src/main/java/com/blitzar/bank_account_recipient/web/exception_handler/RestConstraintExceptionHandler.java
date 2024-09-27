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

    private static final Logger logger = LoggerFactory.getLogger(RestConstraintExceptionHandler.class);
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
        var locale = request.getLocale().orElse(Locale.getDefault());

        // Collect all error messages
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            // Use violation.getMessage() to get the message
            String message = violation.getMessage();
            String localizedMessage = messageSource.getMessage(message, Locale.getDefault()).orElse(message);
            errorMessages.add(localizedMessage); // Add each localized message to the list
            logger.error(localizedMessage); // Log each error message
        }

        // Build the response body in the desired format
        var responseBody = Map.of(
                "_embedded", Map.of("errors", errorMessages.stream()
                        .map(msg -> Map.of("message", msg))
                        .toList()), // Convert messages to the desired format
                "message", "Bad Request" // Include the standard message
        );

        // Return the processed response
        return errorResponseProcessor.processResponse(ErrorContext.builder(request)
                .cause(exception)
                .errorMessages(errorMessages) // Pass the list of error messages directly
                .build(), HttpResponse.badRequest().body(responseBody));
    }
}