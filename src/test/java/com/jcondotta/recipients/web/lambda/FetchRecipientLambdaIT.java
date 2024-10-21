package com.jcondotta.recipients.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.jcondotta.recipients.container.LocalStackTestContainer;
import com.jcondotta.recipients.helper.AddRecipientServiceFacade;
import com.jcondotta.recipients.helper.RecipientTablePurgeService;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.security.AuthenticationService;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.validation.recipient.RecipientsValidator;
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

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class FetchRecipientLambdaIT implements LocalStackTestContainer {

    private static final Context mockLambdaContext = new MockLambdaContext();

    private RecipientsValidator recipientsValidator = new RecipientsValidator();

    private ApiGatewayProxyRequestEventFunction requestEventFunction;
    private APIGatewayProxyRequestEvent requestEvent;
    private APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext;

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    private AuthenticationService authenticationService;

    @BeforeAll
    void beforeAll() {
        requestEventFunction = new ApiGatewayProxyRequestEventFunction(applicationContext);
    }

    @BeforeEach
    void beforeEach() {
        var authenticationResponseDTO = authenticationService.authenticate();
        proxyRequestContext = new APIGatewayProxyRequestEvent.ProxyRequestContext();

        requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(HttpMethod.GET.name())
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
    void shouldReturn200OkAndRecipientList_whenBankAccountIdProvidedWithoutQueryParams() throws JsonProcessingException {
        var expectedRecipients = addRecipientService.addRecipients(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        addRecipientService.addRecipient(TestBankAccount.ITALY, TestRecipient.PATRIZIO);

        var fetchRecipientsAPIPath = RecipientAPIUriBuilder.fetchRecipientsURI(TestBankAccount.BRAZIL.getBankAccountId());

        requestEvent.withPath(fetchRecipientsAPIPath.getRawPath());
        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.OK.getCode());

        RecipientsDTO recipientsDTO = objectMapper.readValue(response.getBody(), RecipientsDTO.class);

        assertThat(recipientsDTO.count()).isEqualTo(expectedRecipients.size());
        assertThat(recipientsDTO.lastEvaluatedKey()).isNull();

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, recipientsDTO.recipients());
    }

    @Test
    void shouldReturn204NoContent_whenNonExistentBankAccountIdIsProvided() {
        var nonExistentBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();

        var fetchRecipientsAPIPath = RecipientAPIUriBuilder.fetchRecipientsURI(nonExistentBankAccountId);

        requestEvent.withPath(fetchRecipientsAPIPath.getRawPath());
        var response = requestEventFunction.handleRequest(requestEvent, mockLambdaContext);

        assertThat(response)
                .as("Verify the response has the correct status code")
                .extracting(APIGatewayProxyResponseEvent::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT.getCode());
    }
}

