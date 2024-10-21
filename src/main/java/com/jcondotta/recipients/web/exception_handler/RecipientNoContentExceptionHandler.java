package com.jcondotta.recipients.web.exception_handler;

import com.jcondotta.recipients.exception.RecipientNoContentException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = { RecipientNoContentException.class })
public class RecipientNoContentExceptionHandler implements ExceptionHandler<RecipientNoContentException, HttpResponse<?>> {

    @Override
    @Status(value = HttpStatus.NO_CONTENT)
    public HttpResponse<?> handle(HttpRequest request, RecipientNoContentException exception) {
        return HttpResponse.noContent();
    }
}