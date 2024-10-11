package com.blitzar.bank_account_recipient.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.blitzar.bank_account_recipient.container.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.helper.AddRecipientServiceFacade;
import com.blitzar.bank_account_recipient.helper.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.security.TokenGeneratorService;
import com.blitzar.bank_account_recipient.web.controller.RecipientAPIUriBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class FetchRecipientLambdaSecurityAccessIT implements LocalStackTestContainer {

    private static final Context mockLambdaContext = new MockLambdaContext();

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;
    private APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext;

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    private TokenGeneratorService tokenGeneratorService;

    @Inject
    private ApplicationContext applicationContext;

    @BeforeAll
    public void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    public void beforeEach() {
        proxyRequestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(HttpMethod.POST.name())
                .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .withRequestContext(proxyRequestContext);
    }

    @AfterEach
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    public void shouldReturn401Unauthorized_whenNoTokenProvided() throws IOException {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        var fetchRecipientsAPIPath = RecipientAPIUriBuilder.fetchRecipientsURI(jeffersonRecipientDTO.getBankAccountId());

        requestEvent.withPath(fetchRecipientsAPIPath.getRawPath());
        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED.getCode())
                .withFailMessage(() -> String.format(
                        "Expected status code to be %d (Unauthorized), but was %d.",
                        HttpStatus.UNAUTHORIZED.getCode(),
                        response.getStatusCode()
                ));
    }
}

