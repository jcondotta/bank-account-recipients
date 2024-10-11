package com.blitzar.bank_account_recipient.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.blitzar.bank_account_recipient.container.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.helper.AddRecipientServiceFacade;
import com.blitzar.bank_account_recipient.helper.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.web.controller.RecipientAPIUriBuilder;
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

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class DeleteRecipientLambdaSecurityAccessIT implements LocalStackTestContainer {

    private static final Context mockLambdaContext = new MockLambdaContext();

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;
    private APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext;

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

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

        var deleteRecipientsURI = RecipientAPIUriBuilder
                .deleteRecipientsURI(jeffersonRecipientDTO.getBankAccountId(), jeffersonRecipientDTO.getRecipientName());

        requestEvent.withPath(deleteRecipientsURI.getRawPath());

        var responseEvent = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);
        assertThat(responseEvent.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }
}

