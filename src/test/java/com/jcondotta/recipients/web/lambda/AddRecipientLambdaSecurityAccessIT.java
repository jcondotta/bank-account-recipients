package com.jcondotta.recipients.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.jcondotta.recipients.container.LocalStackTestContainer;
import com.jcondotta.recipients.helper.RecipientTablePurgeService;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.request.AddRecipientRequest;
import com.jcondotta.recipients.web.controller.RecipientAPIUriBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class AddRecipientLambdaSecurityAccessIT implements LocalStackTestContainer {

    private static final Context mockLambdaContext = new MockLambdaContext();
    
    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();
    
    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;
    private APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @BeforeAll
    void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    void beforeEach() {
        proxyRequestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        requestEvent = new APIGatewayProxyRequestEvent()
                .withPath(RecipientAPIUriBuilder.RECIPIENTS_BASE_PATH_API_V1_MAPPING)
                .withHttpMethod(HttpMethod.POST.name())
                .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .withRequestContext(proxyRequestContext);
    }

    @AfterEach
    void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    void shouldReturn401Unauthorized_whenNoTokenProvided() throws JsonProcessingException {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        requestEvent.setBody(objectMapper.writeValueAsString(addRecipientRequest));

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

