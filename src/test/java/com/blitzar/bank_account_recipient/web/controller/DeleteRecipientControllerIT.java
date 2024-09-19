package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.LocalStackTestContainer;
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
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Clock;
import java.util.Locale;
import java.util.UUID;

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
    private AddRecipientService addRecipientService;

    @Inject
    private Clock testFixedInstantUTC;

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    private RequestSpecification requestSpecification;

    private UUID bankAccountId = UUID.fromString("01920bff-6704-7f02-9671-ddcbbcd33a65");
    private String recipientName = "Jefferson Condotta";
    private String recipientIBAN = "DE00 0000 0000 0000 00";

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(RecipientAPIConstants.RECIPIENT_NAME_API_V1_MAPPING);
    }

    @Test
    public void givenExistentRecipient_whenDeleteRecipient_thenReturnNoContent(){
        var addRecipientRequest = new AddRecipientRequest(recipientName, recipientIBAN);
        addRecipientService.addRecipient(bankAccountId, addRecipientRequest);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .pathParam("recipient-name", addRecipientRequest.name())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(bankAccountId.toString())
                .sortValue(recipientName)
                .build());

        assertThat(recipient).isNull();
    }

    @Test
    public void givenNonExistentRecipient_whenDeleteRecipient_thenReturnNotFound(){
        var addRecipientRequest = new AddRecipientRequest(recipientName, recipientIBAN);
        addRecipientService.addRecipient(bankAccountId, addRecipientRequest);

        var nonExistentRecipientName = "nonExistentRecipientName";
        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .pathParam("recipient-name", nonExistentRecipientName)
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("recipient.notFound", Locale.getDefault(), bankAccountId, nonExistentRecipientName)
                        .orElseThrow()));

        Recipient recipient = dynamoDbTable.getItem(Key.builder()
                .partitionValue(bankAccountId.toString())
                .sortValue(recipientName)
                .build());

        assertThat(recipient).isNotNull();
    }
}
