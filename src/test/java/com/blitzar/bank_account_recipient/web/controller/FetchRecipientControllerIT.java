package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.MongoDBTestContainer;
import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.exception.ResourceNotFoundException;
import com.blitzar.bank_account_recipient.repository.RecipientRepository;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.dto.RecipientsDTO;
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
public class FetchRecipientControllerIT implements MongoDBTestContainer {

    @Inject
    private RecipientRepository recipientRepository;

    @Inject
    private Clock testFixedInstantUTC;

    private RequestSpecification requestSpecification;

    private final String recipientName1 = "Jefferson Condotta";
    private final String recipientIBAN1 = "DE00 0000 0000 0000 00";

    private final String recipientName2 = "Jefferson William";
    private final String recipientIBAN2 = "ES00 0000 0000 0000 00";

    private final Long bankAccountId1 = 1L;
    private final Long bankAccountId2 = 2L;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        recipientRepository.deleteAll();

        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(RecipientAPIConstants.BASE_PATH_API_V1_MAPPING);
    }

    @Test
    public void givenExistentRecipients_whenFetchRecipientsByBankAccountId_thenReturnOk(){
        var recipient1BankAccount1 = recipientRepository.save(new Recipient(recipientName1, recipientIBAN1, bankAccountId1, LocalDateTime.now(testFixedInstantUTC)));
        var recipient2BankAccount1 = recipientRepository.save(new Recipient(recipientName2, recipientIBAN2, bankAccountId1, LocalDateTime.now(testFixedInstantUTC)));
        recipientRepository.save(new Recipient(recipientName1, recipientIBAN1, bankAccountId2, LocalDateTime.now(testFixedInstantUTC)));

        var expectedRecipients = List.of(recipient1BankAccount1, recipient2BankAccount1);

        var recipientsDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId1)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                    .as(RecipientsDTO.class);

        assertThat(recipientsDTO.recipients()).hasSize(expectedRecipients.size());

        expectedRecipients.stream().forEach(expectedRecipient -> {
            RecipientDTO recipientDTO = recipientsDTO.recipients().stream()
                    .filter(dto -> dto.recipientId().equals(expectedRecipient.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Expected recipient with id: " + expectedRecipient.getId() + " is not present."));

            assertAll(
                    () -> assertThat(recipientDTO.recipientId()).isEqualTo(expectedRecipient.getId()),
                    () -> assertThat(recipientDTO.name()).isEqualTo(expectedRecipient.getName()),
                    () -> assertThat(recipientDTO.iban()).isEqualTo(expectedRecipient.getIban()),
                    () -> assertThat(recipientDTO.bankAccountId()).isEqualTo(expectedRecipient.getBankAccountId()),
                    () -> assertThat(recipientDTO.dateCreated()).isEqualTo(LocalDateTime.now(testFixedInstantUTC))
            );
        });
    }

    @Test
    public void givenNonExistentRecipients_whenFetchRecipientsByBankAccountId_thenReturnOk(){
        recipientRepository.save(new Recipient(recipientName1, recipientIBAN1, bankAccountId1, LocalDateTime.now(testFixedInstantUTC)));

        var recipientsDTO = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", bankAccountId2)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
                .extract()
                    .as(RecipientsDTO.class);

        assertThat(recipientsDTO.recipients()).hasSize(0);
    }
}
