package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.MongoDBTestContainer;
import com.blitzar.bank_account_recipient.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Clock;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class AddRecipientControllerIT implements MongoDBTestContainer {

    @Inject
    private RecipientRepository recipientRepository;

    @Inject
    private Clock testFixedInstantUTC;

    private RequestSpecification requestSpecification;

    private String recipientName = "Jefferson Condotta";
    private String recipientIBAN = "DE00 0000 0000 0000 00";
    private Long bankAccountId = 998372L;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(RecipientAPIConstants.BASE_PATH_API_V1_MAPPING);
    }

    @Test
    public void givenValidRequest_whenAddRecipient_thenReturnCreated(){
        var addRecipientRequest = new AddRecipientRequest(recipientName, recipientIBAN);

        var responseRecipientId = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.getCode())
                .body(hasLength(24))
                .extract().asString();

        Recipient recipient = recipientRepository.findById(responseRecipientId)
                .orElseThrow(() -> new ResourceNotFoundException("No recipient has been found with id: " + responseRecipientId));

        assertAll(
                () -> assertThat(recipient.getId()).isEqualTo(responseRecipientId),
                () -> assertThat(recipient.getName()).isEqualTo(addRecipientRequest.name()),
                () -> assertThat(recipient.getIban()).isEqualTo(addRecipientRequest.iban()),
                () -> assertThat(recipient.getBankAccountId()).isEqualTo(bankAccountId),
                () -> assertThat(recipient.getDateCreated()).isEqualTo(LocalDateTime.now(testFixedInstantUTC))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidRecipientName_whenAddRecipient_thenReturnBadRequest(String invalidRecipientName){
        var addRecipientRequest = new AddRecipientRequest(invalidRecipientName, recipientIBAN);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("name: recipient.name.notBlank"));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidIBAN_whenAddRecipient_thenReturnBadRequest(String invalidRecipientIBAN){
        var addRecipientRequest = new AddRecipientRequest(recipientName, invalidRecipientIBAN);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("iban: recipient.iban.notBlank"));
    }
}
