package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.MessageResolver;
import com.blitzar.bank_account_recipient.argumentprovider.BlankAndNonPrintableCharactersArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.service.AddRecipientService;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
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

import java.time.Clock;
import java.util.Locale;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class DeleteRecipientControllerIT implements LocalStackTestContainer {

    @Inject
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    private AddRecipientService addRecipientService;

    @Inject
    private Clock testFixedInstantUTC;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    @Inject
    private RequestSpecification requestSpecification;

    private MessageResolver messageResolver;

    private static final UUID BANK_ACCOUNT_ID = UUID.fromString("01920bff-6704-7f02-9671-ddcbbcd33a65");
    private static final String RECIPIENT_NAME = "Jefferson Condotta";
    private static final String RECIPIENT_IBAN = "GB69 BARC 2004 0183 9936 68";

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

    @Test
    public void shouldReturn204NoContent_whenRecipientExists() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID, RECIPIENT_NAME, RECIPIENT_IBAN);
        addRecipientService.addRecipient(addRecipientRequest);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", addRecipientRequest.bankAccountId())
                .pathParam("recipient-name", addRecipientRequest.recipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(BANK_ACCOUNT_ID.toString())
                .sortValue(RECIPIENT_NAME)
                .build());

        assertThat(recipient).isNull();
    }

    @Test
    public void shouldReturn404NotFound_whenRecipientIsDeleted() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID, RECIPIENT_NAME, RECIPIENT_IBAN);
        addRecipientService.addRecipient(addRecipientRequest);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", addRecipientRequest.bankAccountId())
                .pathParam("recipient-name", addRecipientRequest.recipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", addRecipientRequest.bankAccountId())
                .pathParam("recipient-name", addRecipientRequest.recipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(BlankAndNonPrintableCharactersArgumentProvider.class)
    @Disabled
    public void shouldReturn400BadRequest_whenRecipientNameIsBlank(String invalidRecipientName) {
    }

    @Test
    @Disabled
    public void shouldReturn400BadRequest_whenRecipientNameIsMalicious() {

    }

    @Test
    public void shouldReturn404NotFound_whenRecipientDoesNotExist() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID, RECIPIENT_NAME, RECIPIENT_IBAN);
        addRecipientService.addRecipient(addRecipientRequest);

        var nonExistentRecipientName = "nonExistentRecipientName";
        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", BANK_ACCOUNT_ID)
                .pathParam("recipient-name", nonExistentRecipientName)
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageResolver.getMessage("recipient.notFound", BANK_ACCOUNT_ID, nonExistentRecipientName)));

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(BANK_ACCOUNT_ID.toString())
                .sortValue(RECIPIENT_NAME)
                .build());

        assertThat(recipient).isNotNull();
    }
}
