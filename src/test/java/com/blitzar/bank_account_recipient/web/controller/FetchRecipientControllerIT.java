package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import com.blitzar.bank_account_recipient.service.AddRecipientService;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class FetchRecipientControllerIT implements LocalStackTestContainer {

    @Inject
    private AddRecipientService addRecipientService;

    @Inject
    private Clock testFixedInstantUTC;

    private RequestSpecification requestSpecification;

    private final Long bankAccountId = 1736472L;

    private final String recipientName1 = "Jefferson Condotta";
    private final String recipientIBAN1 = "DE00 0000 0000 0000 00";

    private final String recipientName2 = "Jefferson William";
    private final String recipientIBAN2 = "ES00 0000 0000 0000 00";


    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING);
    }

    @Test
    public void givenExistentRecipients_whenFetchRecipientsByBankAccountId_thenReturnOk(){
        var addRecipientRequest1 = new AddRecipientRequest(recipientName1, recipientIBAN1);
        var recipient1 = addRecipientService.addRecipient(bankAccountId, addRecipientRequest1);

        var addRecipientRequest2 = new AddRecipientRequest(recipientName2, recipientIBAN2);
        var recipient2 = addRecipientService.addRecipient(bankAccountId, addRecipientRequest2);

        var expectedRecipients = List.of(recipient1, recipient2);

        var recipientsDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                    .as(RecipientsDTO.class);

        assertThat(recipientsDTO.recipients()).hasSize(expectedRecipients.size());

        expectedRecipients.stream().forEach(expectedRecipient -> {
            RecipientDTO recipientDTO = recipientsDTO.recipients().stream()
                    .filter(dto -> dto.name().equals(expectedRecipient.getName()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Expected recipient with name: " + expectedRecipient.getName() + " is not present."));

            assertAll(
                    () -> assertThat(recipientDTO.name()).isEqualTo(expectedRecipient.getName()),
                    () -> assertThat(recipientDTO.iban()).isEqualTo(expectedRecipient.getIban()),
                    () -> assertThat(recipientDTO.bankAccountId()).isEqualTo(expectedRecipient.getBankAccountId()),
                    () -> assertThat(recipientDTO.createdAt()).isEqualTo(LocalDateTime.now(testFixedInstantUTC))
            );
        });
    }

    @Test
    public void givenNonExistentRecipients_whenFetchRecipientsByBankAccountId_thenReturnOk(){
        var recipientsDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", Long.MIN_VALUE)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                    .as(RecipientsDTO.class);

        assertThat(recipientsDTO.recipients()).hasSize(0);
    }
}
