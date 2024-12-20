package com.jcondotta.recipients.web.controller;

import com.jcondotta.recipients.container.LocalStackTestContainer;
import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.helper.AddRecipientServiceFacade;
import com.jcondotta.recipients.helper.RecipientTablePurgeService;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.security.AuthenticationService;
import com.jcondotta.recipients.service.cache.RecipientsCacheKey;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.validation.recipient.RecipientsValidator;
import io.lettuce.core.api.sync.RedisCommands;
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
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class FetchRecipientControllerIT implements LocalStackTestContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchRecipientControllerIT.class);

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final int PAGE_LIMIT_2 = 2;

    private final RecipientsValidator recipientsValidator = new RecipientsValidator();

    @Inject
    AddRecipientServiceFacade addRecipientService;

    @Inject
    DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    AuthenticationService authenticationService;

    @Inject
    RedisCommands<String, RecipientsDTO> redisCommands;

    private RequestSpecification requestSpecification;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .basePath(RecipientAPIUriBuilder.BANK_ACCOUNT_API_V1_MAPPING)
                .contentType(ContentType.JSON)
                .auth()
                .oauth2(authenticationService.authenticate().access_token());
    }

    @AfterEach
    void afterEach() {
        recipientTablePurgeService.purgeTable();
    }

    @Test
    void shouldReturnRecipientsDTO_whenNoQueryParamsIsProvided() {
        final var expectedRecipients = addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        addRecipientService.addRecipients(TestBankAccount.ITALY, TestRecipient.JESSICA, TestRecipient.PATRIZIO);

        var recipientsDTO = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", BANK_ACCOUNT_ID_BRAZIL)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("recipients", hasSize(expectedRecipients.size()))
                .body("count", equalTo(expectedRecipients.size()))
                .body("lastEvaluatedKey", nullValue())
                    .extract()
                        .as(RecipientsDTO.class);

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, recipientsDTO.recipients());

        var cacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator
                        .validateDTOsAgainstDTOs(expectedRecipients, cachedRecipientsDTO.recipients()));
    }

    @Test
    void shouldReturnNamePrefixedRecipientsDTO_whenRecipientNameIsProvided() {
        final var prefixRecipientName = "Je";

        var expectedRecipients = addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.JENNIFER);
        addRecipientService.addRecipient(TestBankAccount.ITALY, TestRecipient.JESSICA);

        var recipientsDTO = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", BANK_ACCOUNT_ID_BRAZIL)
                .queryParam("recipientName", prefixRecipientName)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("recipients", hasSize(expectedRecipients.size()))
                .body("count", equalTo(expectedRecipients.size()))
                .body("lastEvaluatedKey", nullValue())
                    .extract()
                        .as(RecipientsDTO.class);

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, recipientsDTO.recipients());

        var queryParams = QueryParams.builder().withRecipientName(prefixRecipientName).build();
        var cacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams).getCacheKey();

        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, cachedRecipientsDTO.recipients()));
    }

    @Test
    void shouldLimitNumberOfRecipientsReturned_whenLimitIsProvided() {
        final var queryParams = QueryParams.builder().withLimit(PAGE_LIMIT_2).build();

        var expectedRecipients = addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JESSICA, TestRecipient.JENNIFER);

        var recipientsDTO = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", BANK_ACCOUNT_ID_BRAZIL)
                .queryParam("limit", queryParams.limit())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("recipients", hasSize(expectedRecipients.size()))
                .body("count", equalTo(expectedRecipients.size()))
                .body("lastEvaluatedKey.bankAccountId", equalTo(BANK_ACCOUNT_ID_BRAZIL.toString()))
                .body("lastEvaluatedKey.recipientName", equalTo(TestRecipient.JEFFERSON.getRecipientName()))
                    .extract()
                        .as(RecipientsDTO.class);

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, recipientsDTO.recipients());

        var cacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, cachedRecipientsDTO.recipients()));
    }

    @Test
    void shouldReturnPagedRecipientsDTO_whenLastEvaluatedKeyIsProvided() {
        addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        var expectedRecipients = addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JESSICA, TestRecipient.JENNIFER);

        var lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON.getRecipientName());
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();

        var recipientsDTO = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", BANK_ACCOUNT_ID_BRAZIL)
                .queryParam("lastEvaluatedKey.bankAccountId", lastEvaluatedKey.bankAccountId())
                .queryParam("lastEvaluatedKey.recipientName", lastEvaluatedKey.recipientName())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("recipients", hasSize(expectedRecipients.size()))
                .body("count", equalTo(expectedRecipients.size()))
                    .extract()
                        .as(RecipientsDTO.class);

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, recipientsDTO.recipients());

        var cacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, cachedRecipientsDTO.recipients()));
    }

    @Test
    void shouldReturn204NoContent_whenNonExistentBankAccountIdIsProvided() {
        given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", BANK_ACCOUNT_ID_BRAZIL)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.NO_CONTENT.getCode());

        var pageIterable = dynamoDbTable.query(QueryConditional.keyEqualTo(builder -> builder
                .partitionValue(BANK_ACCOUNT_ID_BRAZIL.toString())
                .build()));
        assertThat(pageIterable.items()).isEmpty();

        var cacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should have empty values")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> {
                    assertThat(cachedRecipientsDTO.recipients()).isEmpty();
                    assertThat(cachedRecipientsDTO.count()).isZero();
                    assertThat(cachedRecipientsDTO.lastEvaluatedKey()).isNull();
                });
    }

    @Test
    void shouldReturn204NoContent_whenFilteringByNonExistentPrefixRecipientName() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);

        final var nonExistentPrefixRecipientName = "Z";
        LOGGER.debug("Searching for recipients with non-existent prefix name: {}", nonExistentPrefixRecipientName);

        given()
            .spec(requestSpecification)
            .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
            .queryParam("recipientName", nonExistentPrefixRecipientName)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.NO_CONTENT.getCode());

        final char existentPrefixRecipientName = jeffersonRecipientDTO.getRecipientName().charAt(0);
        LOGGER.debug("Searching for recipients with existent prefix name: {}", existentPrefixRecipientName);

        given()
            .spec(requestSpecification)
            .pathParam("bank-account-id", jeffersonRecipientDTO.getBankAccountId())
            .queryParam("recipientName", existentPrefixRecipientName)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.OK.getCode())
            .body("recipients[0].recipientName", equalTo(jeffersonRecipientDTO.getRecipientName()));
    }

    @Test
    void shouldReturnAndCacheRecipientsInPages_whenQueryParamsHasAllParameters() {
        final var prefixRecipientName = "Je";

        var expectedRecipientsPage1 = addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.JENNIFER);
        var expectedRecipientsPage2 = addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JESSICA);
        addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.VIRGINIO, TestRecipient.PATRIZIO);

        LOGGER.debug("Fetching page 1 recipients for bank account ID: {}, recipientNamePrefix: {} and limit: {}",
                BANK_ACCOUNT_ID_BRAZIL, prefixRecipientName, PAGE_LIMIT_2);

        RecipientsDTO recipientsDTOPage1 = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", BANK_ACCOUNT_ID_BRAZIL)
                .queryParam("recipientName", prefixRecipientName)
                .queryParam("limit", PAGE_LIMIT_2)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                    .body("recipients", hasSize(expectedRecipientsPage1.size()))
                    .body("count", equalTo(expectedRecipientsPage1.size()))
                    .body("lastEvaluatedKey.bankAccountId", equalTo(BANK_ACCOUNT_ID_BRAZIL.toString()))
                    .body("lastEvaluatedKey.recipientName", equalTo(TestRecipient.JENNIFER.getRecipientName()))
                    .extract()
                        .as(RecipientsDTO.class);

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsPage1, recipientsDTOPage1.recipients());

        var cacheKeyRecipientsDTOPage1 = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, QueryParams.builder()
                .withRecipientName(prefixRecipientName)
                .withLimit(PAGE_LIMIT_2)
                .build())
                .getCacheKey();

        assertThat(redisCommands.get(cacheKeyRecipientsDTOPage1))
                .as("Cached Page 1 RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsPage1, cachedRecipientsDTO.recipients()));

        LOGGER.debug("Fetching page 2 recipients for bank account ID: {}, recipientNamePrefix: {}, limit: {} and last evaluated key: {}",
                BANK_ACCOUNT_ID_BRAZIL, prefixRecipientName, PAGE_LIMIT_2, recipientsDTOPage1.lastEvaluatedKey());

        var recipientsDTOPage2 = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", BANK_ACCOUNT_ID_BRAZIL)
                .queryParam("recipientName", prefixRecipientName)
                .queryParam("limit", PAGE_LIMIT_2)
                .queryParam("lastEvaluatedKey.bankAccountId", recipientsDTOPage1.lastEvaluatedKey().bankAccountId())
                .queryParam("lastEvaluatedKey.recipientName", recipientsDTOPage1.lastEvaluatedKey().recipientName())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("recipients", hasSize(expectedRecipientsPage2.size()))
                .body("count", equalTo(expectedRecipientsPage2.size()))
                .body("lastEvaluatedKey", nullValue())
                    .extract()
                        .as(RecipientsDTO.class);

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsPage2, recipientsDTOPage2.recipients());

        var cacheKeyRecipientsDTOPage2 = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, QueryParams.builder()
                .withRecipientName(prefixRecipientName)
                .withLimit(PAGE_LIMIT_2)
                .withLastEvaluatedKey(recipientsDTOPage1.lastEvaluatedKey())
                .build())
                .getCacheKey();

        assertThat(redisCommands.get(cacheKeyRecipientsDTOPage2))
                .as("Cached Page 2 RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsPage2, cachedRecipientsDTO.recipients()));

    }
}