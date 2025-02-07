package com.jcondotta.recipients.web.controller;

import com.jcondotta.recipients.container.LocalStackTestContainer;
import com.jcondotta.recipients.helper.AddRecipientServiceFacade;
import com.jcondotta.recipients.helper.RecipientTablePurgeService;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.security.TokenGeneratorService;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class DeleteRecipientControllerSecurityAccessIT implements LocalStackTestContainer {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    @Inject
    AddRecipientServiceFacade addRecipientService;

    @Inject
    RequestSpecification requestSpecification;

    @Inject
    RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    TokenGeneratorService tokenGeneratorService;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .basePath(RecipientAPIUriBuilder.RECIPIENT_NAME_API_V1_MAPPING)
                .contentType(ContentType.JSON);
    }

    @AfterEach
    void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    void shouldReturn401Unauthorized_whenNoTokenProvided() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.getRecipientName())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.getCode())
            .body("message", equalTo(HttpStatus.UNAUTHORIZED.getReason()));
    }

    @Test
    void shouldReturn401Unauthorized_whenTokenIsExpired() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var expiredToken = tokenGeneratorService.generateExpiredToken();

        given()
            .spec(requestSpecification)
                .spec(requestSpecification)
                    .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                    .pathParam("recipient-name", jeffersonRecipientDTO.getRecipientName())
                .auth()
                    .oauth2(expiredToken)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.getCode())
            .body("message", equalTo(HttpStatus.UNAUTHORIZED.getReason()));
    }

    @Test
    void shouldReturn401Unauthorized_whenTokenSignatureIsTampered() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var generatedToken = tokenGeneratorService.generateToken(TokenGeneratorService.DEFAULT_AUTH_USERNAME, 3600);

        var tokenParts = generatedToken.split("\\.");
        var tamperedToken = tokenParts[0] + "." + tokenParts[1] + ".tamperedSignature";

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.getRecipientName())
                    .auth()
                        .oauth2(tamperedToken)
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.getCode())
            .body("message", equalTo(HttpStatus.UNAUTHORIZED.getReason()));
    }

    @Test
    void shouldReturn401Unauthorized_whenTokenExpiresDuringRequest() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        var shortLivedToken = tokenGeneratorService.generateToken(TokenGeneratorService.DEFAULT_AUTH_USERNAME, 1);  // Token expires in 1 second

        LOGGER.debug("Making the first request with valid token for bank account ID: {} and recipient name: {}",
                BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                .pathParam("recipient-name", jeffersonRecipientDTO.getRecipientName())
                .auth()
                    .oauth2(shortLivedToken)
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        LOGGER.debug("Waiting for the token to expire...");

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            LOGGER.debug("Making the second request with expired token for bank account ID: {} and recipient name: {}",
                    BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

            given()
                .spec(requestSpecification)
                    .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
                    .pathParam("recipient-name", jeffersonRecipientDTO.getRecipientName())
                    .auth()
                        .oauth2(shortLivedToken)
            .when()
                .delete()
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode());
        });
    }
}
