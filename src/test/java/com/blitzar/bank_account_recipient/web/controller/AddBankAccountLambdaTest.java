package com.blitzar.bank_account_recipient.web.controller;

//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
//import com.blitzar.bank_account_recipient.LocalStackTestContainer;
//import com.blitzar.bank_account_recipient.domain.Recipient;
//import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.micronaut.context.ApplicationContext;
//import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
//import io.micronaut.function.aws.proxy.MockLambdaContext;
//import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
//import io.micronaut.function.aws.test.annotation.MicronautLambdaTest;
//import io.micronaut.http.HttpHeaders;
//import io.micronaut.http.HttpMethod;
//import io.micronaut.http.HttpStatus;
//import io.micronaut.http.MediaType;
//import jakarta.inject.Inject;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
//import software.amazon.awssdk.enhanced.dynamodb.Key;
//import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;

//@TestInstance(Lifecycle.PER_CLASS)
//@MicronautLambdaTest(transactional = false)
public class AddBankAccountLambdaTest  {

//    private static ApiGatewayProxyRequestEventFunction handler;
//    private static Context lambdaContext;
//
//    private APIGatewayProxyRequestEvent request;
//
//    @Inject
//    protected Clock testClockUTC;
//
//    @Inject
//    protected ObjectMapper objectMapper;
//
//    @Inject
//    protected DynamoDbTable<Recipient> dynamoDbTable;
//
//    private Long bankAccountId = 998372L;
//    private String recipientName = "Jefferson Condotta";
//    private String recipientIBAN = "DE00 0000 0000 0000 00";
//
//    @BeforeAll
//    static void beforeAll(){
//        handler = new ApiGatewayProxyRequestEventFunction();
//        lambdaContext = new MockLambdaContext();
//    }
//
//    @AfterAll
//    static void cleanupSpec() {
//        handler.getApplicationContext().close();
//    }
//
//    @BeforeEach
//    public void beforeEach() {
//        request = new APIGatewayProxyRequestEvent();
//        request.setPath("/api/v1/recipients/bank-account-id/%7Bbank-account-id%7D");
//        request.setHttpMethod(HttpMethod.POST.toString());
//        request.setHeaders(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
//    }
//
//    @Test
//    public void givenValidRequest_whenAddRecipient_thenReturnCreated() throws JsonProcessingException, UnsupportedEncodingException {
//        var addRecipientRequest = new AddRecipientRequest(recipientName, recipientIBAN);
//
//
//        var apiPath = RecipientAPIConstants.BASE_PATH_API_V1_MAPPING.replace("{bank-account-id}", bankAccountId.toString());
//        request.setPath(apiPath);
//        request.setBody(objectMapper.writeValueAsString(addRecipientRequest));
//
//        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(request, lambdaContext);
//        assertAll(
//                () -> assertThat(responseEvent.getStatusCode()).isEqualTo(HttpStatus.CREATED.getCode())
////                () -> assertThat(responseEvent.getBody()).isNotNull()
//        );
//
//        Recipient recipient = dynamoDbTable.getItem(Key.builder()
//                .partitionValue(bankAccountId)
//                .sortValue(recipientName)
//                .build());
//
//
//        PageIterable<Recipient> scan = dynamoDbTable.scan();
//        scan.items().stream().forEach(it -> System.out.println(it.getName()));
//
//        assertThat(recipient).isNotNull();
//        assertAll(
//                () -> assertThat(recipient.getBankAccountId()).isEqualTo(bankAccountId),
//                () -> assertThat(recipient.getName()).isEqualTo(addRecipientRequest.name()),
//                () -> assertThat(recipient.getIban()).isEqualTo(addRecipientRequest.iban()),
//                () -> assertThat(recipient.getCreatedAt()).isEqualTo(LocalDateTime.now(testClockUTC))
//        );
//    }

//
//    @Test
//    public void givenValidRequest_whenAddBankAccount_thenReturnCreated() throws JsonProcessingException {
//        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
//        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);
//
//        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));
//
//        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
//        assertAll(
//                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.getCode()),
//                () -> assertThat(response.getBody()).isNotNull()
//        );
//
//        Long bankAccountId = Long.valueOf(response.getBody());
//        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
//                .orElseThrow();
//
//        assertAll(
//                () -> assertThat(bankAccount.getBankAccountId()).isNotNull(),
//                () -> assertThat(bankAccount.getIban()).isNotNull(),
//                () -> assertThat(bankAccount.getDateOfOpening()).isEqualTo(LocalDateTime.now(testFixedInstantUTC)),
//                () -> assertThat(bankAccount.getAccountHolders()).hasSize(1),
//                () -> assertThat(bankAccount.getAccountHolders().get(0).getAccountHolderName()).isEqualTo(accountHolderName),
//                () -> assertThat(bankAccount.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(accountHolderDateOfBirth),
//                () -> assertThat(bankAccount.getAccountHolders().get(0).getEmailAddress()).isEqualTo(accountHolderEmailAddress)
//        );
//    }
//
//    @Test
//    public void givenEmptyAccountHolders_whenAddBankAccount_thenReturnBadRequest() throws JsonProcessingException {
//        var addBankAccountRequest = new AddBankAccountRequest(List.of());
//
//        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));
//
//        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
//        assertAll(
//                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
//                () -> assertThat(response.getBody()).isNotEmpty()
//        );
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(InvalidStringArgumentProvider.class)
//    public void givenInvalidAccountHolderName_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderName) throws JsonProcessingException {
//        var accountHolder = new AccountHolderRequest(invalidAccountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
//        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);
//
//        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));
//
//        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
//        assertAll(
//                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
//                () -> assertThat(response.getBody()).isNotEmpty()
//        );
//    }
//
//    @Test
//    public void givenNullAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest() throws JsonProcessingException {
//        var accountHolder = new AccountHolderRequest(accountHolderName, null, accountHolderEmailAddress);
//        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);
//
//        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));
//
//        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
//        assertAll(
//                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
//                () -> assertThat(response.getBody()).isNotEmpty()
//        );
//    }
//
//    @Test
//    public void givenFutureAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest() throws JsonProcessingException {
//        var accountHolderDateOfBirth = LocalDate.now().plusDays(1);
//
//        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, accountHolderEmailAddress);
//        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);
//
//        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));
//
//        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
//        assertAll(
//                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
//                () -> assertThat(response.getBody()).isNotEmpty()
//        );
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(InvalidStringArgumentProvider.class)
//    public void givenInvalidAccountHolderEmailAddress_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderEmailAddress) throws JsonProcessingException {
//        var accountHolder = new AccountHolderRequest(accountHolderName, accountHolderDateOfBirth, invalidAccountHolderEmailAddress);
//        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);
//
//        request.setBody(objectMapper.writeValueAsString(addBankAccountRequest));
//
//        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
//        assertAll(
//                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode()),
//                () -> assertThat(response.getBody()).isNotEmpty()
//        );
//    }
}

