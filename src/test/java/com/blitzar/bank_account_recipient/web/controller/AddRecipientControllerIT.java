package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.argumentprovider.validation.BlankAndNonPrintableCharactersArgumentProvider;
import com.blitzar.bank_account_recipient.argumentprovider.validation.iban.InvalidIbanArgumentsProvider;
import com.blitzar.bank_account_recipient.argumentprovider.validation.security.ThreatInputArgumentProvider;
import com.blitzar.bank_account_recipient.container.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.factory.MessageSourceResolver;
import com.blitzar.bank_account_recipient.helper.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.security.AuthenticationService;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import com.blitzar.bank_account_recipient.validation.recipient.RecipientDTOValidator;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddRecipientControllerIT implements LocalStackTestContainer {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    private static final RecipientDTOValidator RECIPIENT_DTO_VALIDATOR = new RecipientDTOValidator();

    @Inject
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    private Clock testClockUTC;

    @Inject
    private MessageSourceResolver messageSourceResolver;

    @Inject
    private RequestSpecification requestSpecification;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    private AuthenticationService authenticationService;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .basePath(RecipientAPIUriBuilder.RECIPIENTS_BASE_PATH_API_V1_MAPPING)
                .contentType(ContentType.JSON)
                .auth()
                    .oauth2(authenticationService.authenticate().access_token());
    }

    @AfterEach
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    public void shouldReturn201Created_whenRequestIsValid() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var expectedLocation = RecipientAPIUriBuilder.fetchRecipientsURI(BANK_ACCOUNT_ID_BRAZIL).getRawPath();
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
                () -> assertThat(recipientDTO.getBankAccountId()).isEqualTo(addRecipientRequest.bankAccountId()),
                () -> assertThat(recipientDTO.getRecipientName()).isEqualTo(addRecipientRequest.recipientName()),
                () -> assertThat(recipientDTO.getRecipientIban()).isEqualTo(addRecipientRequest.recipientIban()),
                () -> assertThat(recipientDTO.getCreatedAt()).isEqualTo(expectedCreatedAt)
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
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.bankAccountId.notNull")));
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
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.recipientName.notBlank")));
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
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
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.recipientName.invalid")));
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
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.recipientName.tooLong")));
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
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.recipientIban.invalid")));
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    public void shouldReturn400BadRequest_whenRecipientIbanIsMalicious(String invalidRecipientIban) {
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
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.recipientIban.invalid")));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidIbanArgumentsProvider.class)
    public void shouldReturn400BadRequest_whenRecipientIbanIsInvalid(String invalidRecipientIban) {
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
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.recipientIban.invalid")));
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
                        messageSourceResolver.getMessage("recipient.bankAccountId.notNull"),
                        messageSourceResolver.getMessage("recipient.recipientName.notBlank"),
                        messageSourceResolver.getMessage("recipient.recipientIban.invalid"))
                );

    }

    @Test
    public void shouldNotCreateDuplicateRecipient_whenSameApiRequestIsSentTwice() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var expectedLocation = RecipientAPIUriBuilder.fetchRecipientsURI(BANK_ACCOUNT_ID_BRAZIL).getRawPath();

        // First API call: Recipient should be created
        var createdRecipientDTO = given()
            .spec(requestSpecification)
            .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.getCode())
                .header("location", equalTo(expectedLocation))
                .extract()
                    .as(RecipientDTO.class);

        // Second API call: Should return the existing recipient (idempotency check)
        var existingRecipientDTO = given()
            .spec(requestSpecification)
            .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                    .as(RecipientDTO.class);

        RECIPIENT_DTO_VALIDATOR.validate(createdRecipientDTO, existingRecipientDTO);

        Recipient fetchedRecipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(addRecipientRequest.bankAccountId().toString())
                .sortValue(addRecipientRequest.recipientName())
                .build());

        RECIPIENT_DTO_VALIDATOR.validate(createdRecipientDTO, fetchedRecipient);
        RECIPIENT_DTO_VALIDATOR.validate(existingRecipientDTO, fetchedRecipient);
    }
}
