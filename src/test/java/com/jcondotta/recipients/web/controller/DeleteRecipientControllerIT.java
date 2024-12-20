package com.jcondotta.recipients.web.controller;

import com.jcondotta.recipients.argument_provider.validation.security.ThreatInputArgumentProvider;
import com.jcondotta.recipients.container.LocalStackTestContainer;
import com.jcondotta.recipients.factory.MessageSourceResolver;
import com.jcondotta.recipients.helper.AddRecipientServiceFacade;
import com.jcondotta.recipients.helper.RecipientTablePurgeService;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.repository.FindRecipientRepository;
import com.jcondotta.recipients.security.AuthenticationService;
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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class DeleteRecipientControllerIT implements LocalStackTestContainer {

    @Inject
    FindRecipientRepository findRecipientRepository;

    @Inject
    AddRecipientServiceFacade addRecipientService;

    @Inject
    RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    MessageSourceResolver messageSourceResolver;

    @Inject
    AuthenticationService authenticationService;

    @Inject
    RequestSpecification requestSpecification;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .basePath(RecipientAPIUriBuilder.RECIPIENT_NAME_API_V1_MAPPING)
                .contentType(ContentType.JSON)
                .auth()
                    .oauth2(authenticationService.authenticate().access_token());
    }

    @AfterEach
    void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    void shouldReturn204NoContent_whenRecipientExists() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.getRecipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        var recipient = findRecipientRepository.findRecipient(jeffersonRecipientDTO.getBankAccountId(), jeffersonRecipientDTO.getRecipientName());
        assertThat(recipient).isNotPresent();
    }

    @Test
    void shouldReturn404NotFound_whenRecipientIsDeleted() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.getRecipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.getRecipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ThreatInputArgumentProvider.class)
    void shouldReturn400BadRequest_whenRecipientNameIsMalicious(String invalidRecipientName) {
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
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.recipientName.invalid",
                        TestBankAccount.BRAZIL.getBankAccountId(), invalidRecipientName)));
    }

    @Test
    void shouldReturn404NotFound_whenRecipientDoesNotExist() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

        var nonExistentRecipientName = "nonExistentRecipientName";
        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                .pathParam("recipient-name", nonExistentRecipientName)
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NOT_FOUND.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(messageSourceResolver.getMessage("recipient.notFound",
                        jeffersonRecipientDTO.getBankAccountId(), nonExistentRecipientName)));

        var recipient = findRecipientRepository.findRecipient(jeffersonRecipientDTO.getBankAccountId(), jeffersonRecipientDTO.getRecipientName());
        assertThat(recipient).isPresent();
    }
}