package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.MongoDBTestContainer;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
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

import java.time.Clock;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class DeleteRecipientControllerIT implements MongoDBTestContainer {

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
                .basePath(RecipientAPIConstants.DELETE_API_V1_MAPPING);
    }

    @Test
    public void givenExistentRecipient_whenDeleteRecipient_thenReturnNoContent(){
        var recipient = new Recipient(recipientName, recipientIBAN, bankAccountId, LocalDateTime.now(testFixedInstantUTC));
        recipientRepository.save(recipient);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", recipient.getBankAccountId())
                .pathParam("recipient-id", recipient.getId())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        assertThat(recipientRepository.find(recipient.getBankAccountId(), recipient.getId())).isEmpty();
    }

    @Test
    public void givenNonExistentRecipient_whenDeleteRecipient_thenReturnNotFound(){
        var recipient = new Recipient(recipientName, recipientIBAN, bankAccountId, LocalDateTime.now(testFixedInstantUTC));
        recipientRepository.save(recipient);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", Integer.MIN_VALUE)
                .pathParam("recipient-id", recipient.getId())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode());

        assertThat(recipientRepository.find(recipient.getBankAccountId(), recipient.getId())).isNotEmpty();
    }
}
