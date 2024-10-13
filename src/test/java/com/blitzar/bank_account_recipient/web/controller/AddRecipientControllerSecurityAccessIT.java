package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.container.LocalStackTestContainer;
import com.blitzar.bank_account_recipient.helper.RecipientTablePurgeService;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.security.TokenGeneratorService;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class AddRecipientControllerSecurityAccessIT implements LocalStackTestContainer {

    private static final Logger logger = LoggerFactory.getLogger(AddRecipientControllerSecurityAccessIT.class);

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();
    private static final String RECIPIENT_IBAN_JEFFERSON = TestRecipient.JEFFERSON.getRecipientIban();

    @Inject
    private RequestSpecification requestSpecification;

    @Inject
    private TokenGeneratorService tokenGeneratorService;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .basePath(RecipientAPIUriBuilder.RECIPIENTS_BASE_PATH_API_V1_MAPPING)
                .contentType(ContentType.JSON);
    }

    @AfterEach
    void afterEach(){
        recipientTablePurgeService.purgeTable();
    }

    @Test
    void shouldReturn401Unauthorized_whenNoTokenProvided() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.getCode())
            .body("message", equalTo(HttpStatus.UNAUTHORIZED.getReason()));
    }

    @Test
    void shouldReturn401Unauthorized_whenTokenIsExpired() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var expiredToken = tokenGeneratorService.generateExpiredToken();

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
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
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);

        var generatedToken = tokenGeneratorService.generateToken(TokenGeneratorService.DEFAULT_AUTH_USERNAME, 3600);

        var tokenParts = generatedToken.split("\\.");
        var tamperedToken = tokenParts[0] + "." + tokenParts[1] + ".tamperedSignature";

        given()
            .spec(requestSpecification)
                .body(addRecipientRequest)
                    .auth()
                        .oauth2(tamperedToken)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.getCode())
            .body("message", equalTo(HttpStatus.UNAUTHORIZED.getReason()));
    }

    @Test
    void shouldReturn401Unauthorized_whenTokenExpiresDuringRequest() {
        var addRecipientRequest = new AddRecipientRequest(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON, RECIPIENT_IBAN_JEFFERSON);
        var shortLivedToken = tokenGeneratorService.generateToken(TokenGeneratorService.DEFAULT_AUTH_USERNAME, 1);  // Token expires in 1 second

        logger.debug("Making the first request with valid token for bank account ID: {} and recipient name: {}",
                BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

        given()
            .spec(requestSpecification)
            .body(addRecipientRequest)
                .auth()
                    .oauth2(shortLivedToken)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.CREATED.getCode());

        logger.debug("Waiting for the token to expire...");

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            logger.debug("Making the second request with expired token for bank account ID: {} and recipient name: {}",
                    BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);

            given()
                .spec(requestSpecification)
                .body(addRecipientRequest)
                    .auth()
                        .oauth2(shortLivedToken)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode());
        });
    }
}
