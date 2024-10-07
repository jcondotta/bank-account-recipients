package com.blitzar.bank_account_recipient.web.controller.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.blitzar.bank_account_recipient.container.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.argumentprovider.validation.BlankValuesArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.helper.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import com.blitzar.bank_account_recipient.web.controller.RecipientAPIConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.MessageSource;
import io.micronaut.function.aws.proxy.MockLambdaContext;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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

    private static Context mockLambdaContext = new MockLambdaContext();

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private Clock testClockUTC;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    private UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    @BeforeAll
    public void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    public void beforeEach() {
        requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(HttpMethod.POST.name())
                .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .withPath(RecipientAPIConstants.RECIPIENTS_BASE_PATH_API_V1_MAPPING);
    }

    @AfterEach
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    public void shouldReturn201Created_whenRequestIsValid() throws IOException {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        requestEvent.setBody(objectMapper.writeValueAsString(addRecipientRequest));

        var expectedLocation = UriBuilder.of(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING)
                .expand(Map.of("bank-account-id", BANK_ACCOUNT_ID_BRAZIL))
                .getRawPath();

        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.CREATED.getCode());

        Optional<String> locationHeader = Optional.ofNullable(response.getHeaders().get(HttpHeaders.LOCATION));
        assertThat(locationHeader)
                .as("Check if the Location header is present")
                .isPresent()
                .hasValue(expectedLocation);

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
                .isEqualTo(exceptionMessageSource.getMessage("recipient.recipientName.notBlank", Locale.getDefault()).orElseThrow());
    }
}

