package com.jcondotta.recipients.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.jcondotta.recipients.container.LocalStackTestContainer;
import com.jcondotta.recipients.helper.AddRecipientServiceFacade;
import com.jcondotta.recipients.helper.RecipientTablePurgeService;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.security.AuthenticationService;
import com.jcondotta.recipients.web.controller.RecipientAPIUriBuilder;
import io.micronaut.context.ApplicationContext;
import io.micronaut.function.aws.proxy.MockLambdaContext;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class DeleteRecipientLambdaIT implements LocalStackTestContainer {

    private static final Context mockLambdaContext = new MockLambdaContext();

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;
    private APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext;

    @Inject
    AddRecipientServiceFacade addRecipientService;

    @Inject
    ApplicationContext applicationContext;

    @Inject
    RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    AuthenticationService authenticationService;

    @BeforeAll
    void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    void beforeEach() {
        var authenticationResponseDTO = authenticationService.authenticate();
        proxyRequestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(HttpMethod.DELETE.name())
                .withHeaders(Map.of(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON,
                        HttpHeaders.AUTHORIZATION, authenticationResponseDTO.buildAuthorizationHeader()))
                .withRequestContext(proxyRequestContext);
    }

    @AfterEach
    void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    void shouldReturn204NoContent_whenRecipientExists() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        var deleteRecipientsURI = RecipientAPIUriBuilder
                .deleteRecipientsURI(jeffersonRecipientDTO.getBankAccountId(), jeffersonRecipientDTO.getRecipientName());

        requestEvent.withPath(deleteRecipientsURI.getRawPath());
        var responseEvent = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(responseEvent.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());
    }

    @Test
    void shouldReturn404NotFound_whenRecipientDoesNotExist() {
        var nonExistentRecipientName = "nonExistentRecipientName";

        var deleteRecipientsURI = RecipientAPIUriBuilder
                .deleteRecipientsURI(TestBankAccount.BRAZIL.getBankAccountId(), nonExistentRecipientName);

        requestEvent.withPath(deleteRecipientsURI.getRawPath());

        var responseEvent = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);
        assertThat(responseEvent.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }
}

