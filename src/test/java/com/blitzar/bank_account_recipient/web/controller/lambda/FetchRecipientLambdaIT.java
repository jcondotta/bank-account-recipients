package com.blitzar.bank_account_recipient.web.controller.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.blitzar.bank_account_recipient.*;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.helper.AddRecipientServiceFacade;
import com.blitzar.bank_account_recipient.service.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import com.blitzar.bank_account_recipient.web.controller.RecipientAPIConstants;
import com.blitzar.bank_account_recipient.validation.RecipientValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class FetchRecipientLambdaIT implements LocalStackTestContainer {

    private static Context mockLambdaContext = new MockLambdaContext();
    private static final UriBuilder DELETE_RECIPIENT_URI_BUILDER = UriBuilder.of(RecipientAPIConstants.RECIPIENT_NAME_API_V1_MAPPING);

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;
    private RecipientValidator recipientValidator;

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @BeforeAll
    public void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    public void beforeEach() {
        recipientValidator = new RecipientValidator();
        requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(HttpMethod.GET.name())
                .withHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }

    @AfterEach
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    void shouldReturn200OkAndRecipientList_whenBankAccountIdProvidedWithoutQueryParams() throws JsonProcessingException {
        var expectedRecipients = addRecipientService.addRecipients(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        addRecipientService.addRecipient(TestBankAccount.ITALY, TestRecipient.PATRIZIO);

        var fetchRecipientsAPIPath = UriBuilder.of(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING)
                .expand(Map.of("bank-account-id", TestBankAccount.BRAZIL.getBankAccountId()))
                .getRawPath();

        requestEvent.withPath(fetchRecipientsAPIPath);
        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.OK.getCode());

        RecipientsDTO recipientsDTO = objectMapper.readValue(response.getBody(), RecipientsDTO.class);

        assertThat(recipientsDTO.count()).isEqualTo(expectedRecipients.size());
        assertThat(recipientsDTO.lastEvaluatedKey()).isNull();

        recipientValidator.validateRecipients(expectedRecipients, recipientsDTO.recipients());
    }

    @Test
    void shouldReturn204NoContent_whenNonExistentBankAccountIdIsProvided() {
        var nonExistentBankAccountId = TestBankAccount.BRAZIL.getBankAccountId().toString();

        var fetchRecipientsAPIPath = UriBuilder.of(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING)
                .expand(Map.of("bank-account-id", nonExistentBankAccountId))
                .getRawPath();

        requestEvent.withPath(fetchRecipientsAPIPath);
        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT.getCode());
    }
}
