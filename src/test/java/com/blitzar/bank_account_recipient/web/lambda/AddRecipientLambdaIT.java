package com.blitzar.bank_account_recipient.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.blitzar.bank_account_recipient.argumentprovider.validation.BlankValuesArgumentProvider;
import com.blitzar.bank_account_recipient.container.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.factory.MessageSourceResolver;
import com.blitzar.bank_account_recipient.helper.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.security.AuthenticationService;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddRecipientLambdaIT implements LocalStackTestContainer {

    private static final Context mockLambdaContext = new MockLambdaContext();

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;
    private APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private Clock testClockUTC;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private MessageSourceResolver messageSourceResolver;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    @BeforeAll
    public void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    public void beforeEach() {
        var authenticationResponseDTO = authenticationService.authenticate();
        proxyRequestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        requestEvent = new APIGatewayProxyRequestEvent()
                .withPath(RecipientAPIUriBuilder.RECIPIENTS_BASE_PATH_API_V1_MAPPING)
                .withHttpMethod(HttpMethod.POST.name())
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
    public void shouldReturn201Created_whenRequestIsValid() throws IOException {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        requestEvent.setBody(objectMapper.writeValueAsString(addRecipientRequest));

        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.CREATED.getCode());

        var recipient = objectMapper.readValue(response.getBody(), Recipient.class);
        assertAll(
                () -> assertThat(recipient.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID_BRAZIL),
                () -> assertThat(recipient.getRecipientName()).isEqualTo(addRecipientRequest.recipientName()),
                () -> assertThat(recipient.getRecipientIban()).isEqualTo(addRecipientRequest.recipientIban()),
                () -> assertThat(recipient.getCreatedAt()).isEqualTo(LocalDateTime.now(testClockUTC))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    public void shouldReturn400BadRequest_whenRecipientNameIsBlank(String invalidRecipientName) throws IOException {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName, RECIPIENT_IBAN_JEFFERSON);
        requestEvent.setBody(objectMapper.writeValueAsString(addRecipientRequest));

        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.BAD_REQUEST.getCode());

        var responseBodyJSON = objectMapper.readTree(response.getBody());
        var errorMessage = responseBodyJSON.at("/_embedded/errors/0/message").asText();

        assertThat(errorMessage)
                .as("Verify the error message in the response body")
                .isEqualTo(messageSourceResolver.getMessage("recipient.recipientName.notBlank", Locale.getDefault()));
    }
}

