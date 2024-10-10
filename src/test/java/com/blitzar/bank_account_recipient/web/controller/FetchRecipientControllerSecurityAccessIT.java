package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.container.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.helper.AddRecipientServiceFacade;
import com.blitzar.bank_account_recipient.helper.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.security.TokenGeneratorService;
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
public class FetchRecipientControllerSecurityAccessIT implements LocalStackTestContainer {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private RequestSpecification requestSpecification;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    private TokenGeneratorService tokenGeneratorService;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .basePath(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING)
                .contentType(ContentType.JSON);
    }

    @AfterEach
    public void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    public void shouldReturn401Unauthorized_whenNoTokenProvided() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
        .when()
            .delete()
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.getCode())
            .body("message", equalTo(HttpStatus.UNAUTHORIZED.getReason()));
    }

    @Test
    public void shouldReturn401Unauthorized_whenTokenIsExpired() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var expiredToken = tokenGeneratorService.generateExpiredToken();

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
            .auth()
                .oauth2(expiredToken)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.getCode())
            .body("message", equalTo(HttpStatus.UNAUTHORIZED.getReason()));
    }

    @Test
    public void shouldReturn401Unauthorized_whenTokenSignatureIsTampered() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var generatedToken = tokenGeneratorService.generateToken(TokenGeneratorService.DEFAULT_AUTH_USERNAME, 3600);

        var tokenParts = generatedToken.split("\\.");
        var tamperedToken = tokenParts[0] + "." + tokenParts[1] + ".tamperedSignature";

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
                    .auth()
                        .oauth2(tamperedToken)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.getCode())
            .body("message", equalTo(HttpStatus.UNAUTHORIZED.getReason()));
    }

    @Test
    public void shouldReturn401Unauthorized_whenTokenExpiresDuringRequest() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        var shortLivedToken = tokenGeneratorService.generateToken(TokenGeneratorService.DEFAULT_AUTH_USERNAME, 1);  // Token expires in 1 second

        logger.debug("Making the first request with valid token for bank account ID: {}", BANK_ACCOUNT_ID_BRAZIL);

        given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
                .auth()
                    .oauth2(shortLivedToken)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode());

        logger.debug("Waiting for the token to expire...");

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            logger.debug("Making the second request with expired token for bank account ID: {}", BANK_ACCOUNT_ID_BRAZIL);

            given()
                .spec(requestSpecification)
                    .pathParam("bank-account-id", jeffersonRecipientDTO.bankAccountId())
                    .auth()
                        .oauth2(shortLivedToken)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode());
        });
    }
}
