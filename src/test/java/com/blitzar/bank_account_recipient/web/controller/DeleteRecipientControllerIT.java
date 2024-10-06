package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.MessageResolver;
import com.blitzar.bank_account_recipient.argumentprovider.malicious.MaliciousInputArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.helper.AddRecipientServiceFacade;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.RecipientTablePurgeService;
import io.micronaut.context.MessageSource;
import io.micronaut.http.HttpStatus;
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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class DeleteRecipientControllerIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    @Inject
    private RequestSpecification requestSpecification;

    private MessageResolver messageResolver;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.messageResolver = new MessageResolver(exceptionMessageSource);
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(RecipientAPIConstants.RECIPIENT_NAME_API_V1_MAPPING);
    }

    @AfterEach
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    public void shouldReturn204NoContent_whenRecipientExists() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.recipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(jeffersonRecipientDTO.bankAccountId().toString())
                .sortValue(jeffersonRecipientDTO.recipientName())
                .build());

        assertThat(recipient).isNull();
    }

    @Test
    public void shouldReturn404NotFound_whenRecipientIsDeleted() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.recipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.recipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(MaliciousInputArgumentProvider.class)
    public void shouldReturn400BadRequest_whenRecipientNameIsMalicious(String invalidRecipientName) {
        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", TestBankAccount.BRAZIL.getBankAccountId())
                .pathParam("recipient-name", invalidRecipientName)
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.recipientName.invalid",
                        TestBankAccount.BRAZIL.getBankAccountId(), invalidRecipientName)));
    }

    @Test
    public void shouldReturn404NotFound_whenRecipientDoesNotExist() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        var nonExistentRecipientName = "nonExistentRecipientName";
        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
                .pathParam("recipient-name", nonExistentRecipientName)
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.notFound",
                        jeffersonRecipientDTO.bankAccountId(), nonExistentRecipientName)));

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(jeffersonRecipientDTO.bankAccountId().toString())
                .sortValue(jeffersonRecipientDTO.recipientName())
                .build());

        assertThat(recipient).isNotNull();
    }
}
