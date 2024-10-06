package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.MessageResolver;
import com.blitzar.bank_account_recipient.argumentprovider.BlankAndNonPrintableCharactersArgumentProvider;
import com.blitzar.bank_account_recipient.argumentprovider.InvalidIBANArgumentProvider;
import com.blitzar.bank_account_recipient.argumentprovider.malicious.MaliciousInputArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import io.micronaut.context.MessageSource;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddRecipientControllerIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    private Clock testClockUTC;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    @Inject
    private RequestSpecification requestSpecification;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    private MessageResolver messageResolver;

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.messageResolver = new MessageResolver(exceptionMessageSource);
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(RecipientAPIConstants.RECIPIENTS_BASE_PATH_API_V1_MAPPING);
    }

    @AfterEach
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    public void shouldReturn201Created_whenRequestIsValid() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var expectedLocation = UriBuilder.of(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING)
                .expand(Map.of("bank-account-id", BANK_ACCOUNT_ID_BRAZIL))
                .getRawPath();


        var expectedCreatedAt = LocalDateTime.now(testClockUTC);

        var recipientDTO = given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.getCode())
            .header("location", equalTo(expectedLocation))
                .extract()
                    .as(RecipientDTO.class);

        assertAll(
                () -> assertThat(recipientDTO.bankAccountId()).isEqualTo(addRecipientRequest.bankAccountId()),
                () -> assertThat(recipientDTO.recipientName()).isEqualTo(addRecipientRequest.recipientName()),
                () -> assertThat(recipientDTO.recipientIban()).isEqualTo(addRecipientRequest.recipientIban()),
                () -> assertThat(recipientDTO.createdAt()).isEqualTo(expectedCreatedAt)
        );

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(BANK_ACCOUNT_ID_BRAZIL.toString())
                .sortValue(RECIPIENT_NAME_JEFFERSON)
                .build());

        assertThat(recipient).isNotNull();
        assertAll(
                () -> assertThat(recipient.getBankAccountId()).isEqualTo(addRecipientRequest.bankAccountId()),
                () -> assertThat(recipient.getRecipientName()).isEqualTo(addRecipientRequest.recipientName()),
                () -> assertThat(recipient.getRecipientIban()).isEqualTo(addRecipientRequest.recipientIban()),
                () -> assertThat(recipient.getCreatedAt()).isEqualTo(expectedCreatedAt)
        );
    }

    @Test
    public void shouldReturn400BadRequest_whenBankAccountIdIsNull() {
        var addRecipientRequest = new AddRecipientRequest(null, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.bankAccountId.notNull")));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    public void shouldReturn400BadRequest_whenRecipientNameIsBlank(String invalidRecipientName) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName, RECIPIENT_IBAN_JEFFERSON);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.recipientName.notBlank")));
    }

    @ParameterizedTest
    @ArgumentsSource(MaliciousInputArgumentProvider.class)
    public void shouldReturn400BadRequest_whenRecipientNameIsMalicious(String invalidRecipientName) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, invalidRecipientName, RECIPIENT_IBAN_JEFFERSON);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.recipientName.invalid")));
    }

    @Test
    public void shouldReturn400BadRequest_whenRecipientNameIsLongerThan50Characters() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, "J".repeat(51), RECIPIENT_IBAN_JEFFERSON);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.recipientName.tooLong")));
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    public void shouldReturn400BadRequest_whenRecipientIbanIsBlank(String invalidRecipientIban) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIban);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.recipientIban.invalid")));
    }

    @ParameterizedTest
    @ArgumentsSource(MaliciousInputArgumentProvider.class)
    public void shouldReturn400BadRequest_whenRecipientIBANIsMalicious(String invalidRecipientIBAN) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIBAN);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.recipientIban.invalid")));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidIBANArgumentProvider.class)
    public void shouldReturn400BadRequest_whenRecipientIBANIsInvalid(String invalidRecipientIBAN) {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, invalidRecipientIBAN);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.recipientIban.invalid")));
    }

    @Test
    void shouldReturn400BadRequest_whenAllFieldsAreNull() {
        var addRecipientRequest = new AddRecipientRequest(null, null, null);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(3))
                .body("errors.message", containsInAnyOrder(
                        messageResolver.getMessage("recipient.bankAccountId.notNull"),
                        messageResolver.getMessage("recipient.recipientName.notBlank"),
                        messageResolver.getMessage("recipient.recipientIban.invalid"))
                );

    }
}
