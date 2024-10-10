package com.blitzar.bank_account_recipient.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.blitzar.bank_account_recipient.container.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.helper.AddRecipientServiceFacade;
import com.blitzar.bank_account_recipient.helper.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.security.AuthenticationService;
import com.blitzar.bank_account_recipient.web.controller.RecipientAPIConstants;
import io.micronaut.context.ApplicationContext;
import io.micronaut.function.aws.proxy.MockLambdaContext;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class DeleteRecipientLambdaIT implements LocalStackTestContainer {

    private static final Context mockLambdaContext = new MockLambdaContext();
    private static final UriBuilder DELETE_RECIPIENT_URI_BUILDER = UriBuilder.of(RecipientAPIConstants.RECIPIENT_NAME_API_V1_MAPPING);

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;
    private APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext;

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    private AuthenticationService authenticationService;

    @BeforeAll
    public void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    public void beforeEach() {
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
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    public void shouldReturn204NoContent_whenRecipientExists() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        var deleteRecipientURI = DELETE_RECIPIENT_URI_BUILDER.expand(
                Map.of(
                        "bank-account-id", jeffersonRecipientDTO.bankAccountId().toString(),
                        "recipient-name", jeffersonRecipientDTO.recipientName())
                );

        requestEvent.withPath(deleteRecipientURI.getRawPath());
        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT.getCode());
    }

    @Test
    public void shouldReturn404NotFound_whenRecipientDoesNotExist() {
        var nonExistentRecipientName = "nonExistentRecipientName";

        var deleteRecipientURI = DELETE_RECIPIENT_URI_BUILDER.expand(
                Map.of(
                        "bank-account-id", TestBankAccount.BRAZIL.getBankAccountId(),
                        "recipient-name", nonExistentRecipientName)
                );

        requestEvent.withPath(deleteRecipientURI.getRawPath());
        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }
}

