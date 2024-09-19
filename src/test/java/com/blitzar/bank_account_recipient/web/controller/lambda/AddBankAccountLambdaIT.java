package com.blitzar.bank_account_recipient.web.controller.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.blitzar.bank_account_recipient.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import com.blitzar.bank_account_recipient.web.controller.RecipientAPIConstants;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddBankAccountLambdaIT implements LocalStackTestContainer {

    private static Context mockLambdaContext = new MockLambdaContext();

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private Clock testClockUTC;

    @Inject
    private ApplicationContext applicationContext;

    private Long bankAccountId = 998372L;
    private String recipientName = "Jefferson Condotta";
    private String recipientIBAN = "BR34 9984 1263 6653 4422";

    @BeforeAll
    public void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    public void beforeEach() {
        requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(HttpMethod.POST.name())
                .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .withPath(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING.replace("{bank-account-id}", bankAccountId.toString()));
    }

    @Test
    public void givenValidRequest_whenAddRecipient_thenReturnCreated() throws JsonProcessingException {
        var addRecipientRequest = new AddRecipientRequest(recipientName, recipientIBAN);
        requestEvent.setBody(objectMapper.writeValueAsString(addRecipientRequest));

        APIGatewayProxyResponseEvent response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.getBody()).isNotBlank();

        var recipient = objectMapper.readValue(response.getBody(), Recipient.class);
        assertAll(
                () -> assertThat(recipient.getBankAccountId()).isEqualTo(bankAccountId),
                () -> assertThat(recipient.getName()).isEqualTo(addRecipientRequest.name()),
                () -> assertThat(recipient.getIban()).isEqualTo(addRecipientRequest.iban()),
                () -> assertThat(recipient.getCreatedAt()).isEqualTo(LocalDateTime.now(testClockUTC))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidRecipientName_whenAddRecipient_thenReturnBadRequest(String invalidRecipientName) throws JsonProcessingException {
        var addRecipientRequest = new AddRecipientRequest(invalidRecipientName, recipientIBAN);
        requestEvent.setBody(objectMapper.writeValueAsString(addRecipientRequest));

        APIGatewayProxyResponseEvent response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
                () -> assertThat(response.getBody()).isNotEmpty()
        );
    }
}

