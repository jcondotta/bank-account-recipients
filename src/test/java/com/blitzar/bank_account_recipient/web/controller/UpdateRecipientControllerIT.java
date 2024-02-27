package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.MongoDBTestContainer;
import com.blitzar.bank_account_recipient.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import com.blitzar.bank_account_recipient.service.request.UpdateRecipientRequest;
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
public class UpdateRecipientControllerIT implements MongoDBTestContainer {

    @Inject
    private RecipientRepository recipientRepository;

    @Inject
    private Clock testFixedInstantUTC;

    private RequestSpecification requestSpecification;

    private String currentRecipientName = "Jefferson Condotta";
    private String currentRecipientIBAN = "DE00 0000 0000 0000 00";

    private String updatedRecipientName = "Jefferson William";
    private String updatedRecipientIBAN = "ES99 9999 9999 9999 99";

    private Long bankAccountId = 998372L;
    private Recipient currentRecipient;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        currentRecipient = new Recipient(currentRecipientName, currentRecipientIBAN, bankAccountId, LocalDateTime.now(testFixedInstantUTC));
        currentRecipient = recipientRepository.save(currentRecipient);
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(RecipientAPIConstants.GET_RECIPIENT_API_V1_MAPPING);
    }

    @Test
    public void givenValidRequest_whenUpdateRecipient_thenReturnOk(){
        var updateRecipientRequest = new UpdateRecipientRequest(updatedRecipientName, updatedRecipientIBAN);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", currentRecipient.getBankAccountId())
                .pathParam("recipient-id", currentRecipient.getId())
                .body(updateRecipientRequest)
        .when()
            .put()
        .then()
            .statusCode(HttpStatus.OK.getCode());

        var updatedRecipient = recipientRepository.findById(currentRecipient.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No recipient has been found with id: " + currentRecipient.getId()));

        assertAll(
                () -> assertThat(updatedRecipient.getName()).isEqualTo(updateRecipientRequest.name()),
                () -> assertThat(updatedRecipient.getIban()).isEqualTo(updateRecipientRequest.iban()),
                () -> assertThat(updatedRecipient.getBankAccountId()).isEqualTo(currentRecipient.getBankAccountId())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidRecipientName_whenUpdateRecipient_thenReturnBadRequest(String invalidRecipientName){
        var updateRecipientRequest = new UpdateRecipientRequest(invalidRecipientName, updatedRecipientIBAN);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", currentRecipient.getBankAccountId())
                .pathParam("recipient-id", currentRecipient.getId())
                .body(updateRecipientRequest)
        .when()
            .put()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("name: recipient.name.notBlank"));

        var recipient = recipientRepository.findById(currentRecipient.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No recipient has been found with id: " + currentRecipient.getId()));

        assertAll(
                () -> assertThat(recipient.getName()).isEqualTo(currentRecipientName),
                () -> assertThat(recipient.getIban()).isEqualTo(currentRecipientIBAN),
                () -> assertThat(recipient.getBankAccountId()).isEqualTo(bankAccountId)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidIBAN_whenAddRecipient_thenReturnBadRequest(String invalidRecipientIBAN){
        var updateRecipientRequest = new UpdateRecipientRequest(updatedRecipientName, invalidRecipientIBAN);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", currentRecipient.getBankAccountId())
                .pathParam("recipient-id", currentRecipient.getId())
                .body(updateRecipientRequest)
        .when()
            .put()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("iban: recipient.iban.notBlank"));

        var recipient = recipientRepository.findById(currentRecipient.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No recipient has been found with id: " + currentRecipient.getId()));

        assertAll(
                () -> assertThat(recipient.getName()).isEqualTo(currentRecipientName),
                () -> assertThat(recipient.getIban()).isEqualTo(currentRecipientIBAN),
                () -> assertThat(recipient.getBankAccountId()).isEqualTo(bankAccountId)
        );
    }
}
