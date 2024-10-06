package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.helper.AddRecipientServiceFacade;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
import com.blitzar.bank_account_recipient.validation.RecipientValidator;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.Clock;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class FetchRecipientControllerIT implements LocalStackTestContainer {

    private static final Logger logger = LoggerFactory.getLogger(FetchRecipientControllerIT.class);
    private static final int PAGE_LIMIT_2 = 2;

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    private Clock testFixedInstantUTC;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    private RecipientValidator recipientValidator;
    private RequestSpecification requestSpecification;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.recipientValidator = new RecipientValidator();
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING);
    }

    @AfterEach
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    void shouldReturn200OkAndRecipientList_whenBankAccountIdProvidedWithoutQueryParams() {
        var expectedRecipients = addRecipientService.addRecipients(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        addRecipientService.addRecipient(TestBankAccount.ITALY, TestRecipient.JESSICA);

        var recipientsDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", TestBankAccount.BRAZIL.getBankAccountId())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                    .body("recipients", hasSize(expectedRecipients.size()))
                    .body("count", equalTo(expectedRecipients.size()))
                    .body("lastEvaluatedKey", nullValue())
                    .extract()
                        .as(RecipientsDTO.class);

        recipientValidator.validateRecipients(expectedRecipients, recipientsDTO.recipients());
    }

    @Test
    void shouldReturn200OKAndRecipientListWithRecipientNamePrefix_whenRecipientNameIsProvided() {
        var expectedRecipients = addRecipientService.addRecipients(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON, TestRecipient.JESSICA);
        addRecipientService.addRecipient(TestBankAccount.ITALY, TestRecipient.PATRIZIO);

        final var prefixRecipientName = "Je";

        var recipientsDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", TestBankAccount.BRAZIL.getBankAccountId())
                .queryParam("recipientName", prefixRecipientName)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                    .body("recipients", hasSize(expectedRecipients.size()))
                    .body("count", equalTo(expectedRecipients.size()))
                    .body("lastEvaluatedKey", nullValue())
                        .extract()
                            .as(RecipientsDTO.class);

        recipientValidator.validateRecipients(expectedRecipients, recipientsDTO.recipients());
    }

    @Test
    void shouldReturn204NoContent_whenNonExistentBankAccountIdIsProvided() {
        var nonExistentBankAccountId = TestBankAccount.BRAZIL.getBankAccountId().toString();

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", nonExistentBankAccountId)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.NO_CONTENT.getCode());

        var pageIterable = dynamoDbTable.query(QueryConditional
                .keyEqualTo(builder -> builder.partitionValue(nonExistentBankAccountId).build()));

        assertThat(pageIterable.items()).isEmpty();
    }

    @Test
    public void shouldReturn204NoContent_whenFilteringByNonExistentPrefixRecipientName(){
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        final var nonExistentPrefixRecipientName = "Z";
        logger.debug("Searching for recipients with non-existent prefix name: {}", nonExistentPrefixRecipientName);

        given()
            .spec(requestSpecification)
            .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
            .queryParam("recipientName", nonExistentPrefixRecipientName)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        final char existentPrefixRecipientName = jeffersonRecipientDTO.recipientName().charAt(0);
        logger.debug("Searching for recipients with existent prefix name: {}", existentPrefixRecipientName);

        given()
            .spec(requestSpecification)
            .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
            .queryParam("recipientName", existentPrefixRecipientName)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
            .body("recipients[0].recipientName", equalTo(jeffersonRecipientDTO.recipientName()));
    }

    @Test
    public void shouldReturnPagedRecipients_whenLimitQueryParamIsSet_forSubsequentRequests(){
        final var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();

        var expectedRecipientsPage1 = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        var expectedRecipientsPage2 = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JESSICA, TestRecipient.PATRIZIO);
        var expectedRecipientsPage3 = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.VIRGINIO);

        logger.debug("Fetching page 1 recipients for bank account ID: {}", brazilBankAccountId);
        RecipientsDTO recipientsDTOPage1 = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
                .queryParam("limit", PAGE_LIMIT_2)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                    .body("recipients", hasSize(expectedRecipientsPage1.size()))
                    .body("count", equalTo(2))
                    .body("lastEvaluatedKey.bankAccountId", equalTo(brazilBankAccountId.toString()))
                    .body("lastEvaluatedKey.recipientName", equalTo(TestRecipient.JEFFERSON.getRecipientName()))
                    .extract()
                        .as(RecipientsDTO.class);

        recipientValidator.validateRecipients(expectedRecipientsPage1, recipientsDTOPage1.recipients());

        logger.debug("Fetching page 2 recipients for bank account ID: {} with last evaluated key: {}", brazilBankAccountId, TestRecipient.JEFFERSON.getRecipientName());
        var recipientsDTOPage2 = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
                .queryParam("limit", PAGE_LIMIT_2)
                .queryParam("lastEvaluatedKey.bankAccountId", brazilBankAccountId)
                .queryParam("lastEvaluatedKey.recipientName", TestRecipient.JEFFERSON.getRecipientName())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("recipients", hasSize(expectedRecipientsPage2.size()))
                .body("count", equalTo(expectedRecipientsPage2.size()))
                .body("lastEvaluatedKey.bankAccountId", equalTo(brazilBankAccountId.toString()))
                .body("lastEvaluatedKey.recipientName", equalTo(TestRecipient.PATRIZIO.getRecipientName()))
                    .extract()
                        .as(RecipientsDTO.class);

        recipientValidator.validateRecipients(expectedRecipientsPage2, recipientsDTOPage2.recipients());

        logger.debug("Fetching page 3 recipients for bank account ID: {} with last evaluated key: {}", brazilBankAccountId, TestRecipient.PATRIZIO.getRecipientName());
        var recipientsDTOPage3 = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
                .queryParam("limit", PAGE_LIMIT_2)
                .queryParam("lastEvaluatedKey.bankAccountId", brazilBankAccountId)
                .queryParam("lastEvaluatedKey.recipientName", TestRecipient.PATRIZIO.getRecipientName())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("recipients", hasSize(expectedRecipientsPage3.size()))
                .body("count", equalTo(expectedRecipientsPage3.size()))
                .body("lastEvaluatedKey", nullValue())
                    .extract()
                        .as(RecipientsDTO.class);

        recipientValidator.validateRecipients(expectedRecipientsPage3, recipientsDTOPage3.recipients());
    }
}