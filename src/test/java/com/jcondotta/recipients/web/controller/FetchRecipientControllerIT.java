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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class FetchRecipientControllerIT implements LocalStackTestContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchRecipientControllerIT.class);
    private static final int PAGE_LIMIT_2 = 2;

    private final RecipientsValidator recipientsValidator = new RecipientsValidator();

    @Inject
    private AddRecipientServiceFacade addRecipientService;

    @Inject
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Inject
    private RecipientTablePurgeService recipientTablePurgeService;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private RedisCommands<String, RecipientsDTO> redisCommands;

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
        redisCommands.scan().getKeys().forEach(key -> redisCommands.del(key));
    }

    @Test
    void shouldReturnRecipientsDTO_whenNoQueryParamsIsProvided() {
        final var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();

        final var expectedRecipients = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        addRecipientService.addRecipients(TestBankAccount.ITALY, TestRecipient.JESSICA, TestRecipient.PATRIZIO);

        var recipientsDTO = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
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

        var cacheKey = new RecipientsCacheKey(brazilBankAccountId).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator
                        .validateDTOsAgainstDTOs(expectedRecipients, cachedRecipientsDTO.recipients()));
    }

    @Test
    void shouldReturnNamePrefixedRecipientsDTO_whenRecipientNameIsProvided() {
        final var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();
        final var prefixRecipientName = "Je";

        var expectedRecipients = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JEFFERSON, TestRecipient.JENNIFER);
        addRecipientService.addRecipient(TestBankAccount.ITALY, TestRecipient.JESSICA);

        var recipientsDTO = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
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
        var cacheKey = new RecipientsCacheKey(brazilBankAccountId, queryParams).getCacheKey();

        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, cachedRecipientsDTO.recipients()));
    }

    @Test
    void shouldLimitNumberOfRecipientsReturned_whenLimitIsProvided() {
        final var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();
        final var queryParams = QueryParams.builder().withLimit(PAGE_LIMIT_2).build();

        var expectedRecipients = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JESSICA, TestRecipient.JENNIFER);

        var recipientsDTO = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
                .queryParam("limit", queryParams.limit())
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("recipients", hasSize(expectedRecipients.size()))
                .body("count", equalTo(expectedRecipients.size()))
                .body("lastEvaluatedKey.bankAccountId", equalTo(brazilBankAccountId.toString()))
                .body("lastEvaluatedKey.recipientName", equalTo(TestRecipient.JEFFERSON.getRecipientName()))
                    .extract()
                        .as(RecipientsDTO.class);

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, recipientsDTO.recipients());

        var cacheKey = new RecipientsCacheKey(brazilBankAccountId, queryParams).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, cachedRecipientsDTO.recipients()));
    }

    @Test
    void shouldReturnPagedRecipientsDTO_whenLastEvaluatedKeyIsProvided() {
        final var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();

        addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JEFFERSON, TestRecipient.INDALECIO);
        var expectedRecipients = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JESSICA, TestRecipient.JENNIFER);

        var lastEvaluatedKey = new LastEvaluatedKey(brazilBankAccountId, TestRecipient.JEFFERSON.getRecipientName());
        final var queryParams = QueryParams.builder().withLastEvaluatedKey(lastEvaluatedKey).build();

        var recipientsDTO = given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
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

        var cacheKey = new RecipientsCacheKey(brazilBankAccountId, queryParams).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipients, cachedRecipientsDTO.recipients()));
    }

    @Test
    void shouldReturn204NoContent_whenNonExistentBankAccountIdIsProvided() {
        var nonExistentBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();

        given()
                .spec(requestSpecification)
                .pathParam("bank-account-id", nonExistentBankAccountId)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.NO_CONTENT.getCode());

        var pageIterable = dynamoDbTable.query(QueryConditional.keyEqualTo(builder -> builder
                .partitionValue(nonExistentBankAccountId.toString())
                .build()));
        assertThat(pageIterable.items()).isEmpty();

        var cacheKey = new RecipientsCacheKey(nonExistentBankAccountId).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cached RecipientsDTO should have empty values")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> {
                    assertThat(cachedRecipientsDTO.recipients().isEmpty());
                    assertThat(cachedRecipientsDTO.count()).isZero();
                    assertThat(cachedRecipientsDTO.lastEvaluatedKey()).isNull();
                });
    }

    @Test
    void shouldReturn204NoContent_whenFilteringByNonExistentPrefixRecipientName() {
        var jeffersonRecipientDTO = addRecipientService.addRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);

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
        final var brazilBankAccountId = TestBankAccount.BRAZIL.getBankAccountId();
        final var prefixRecipientName = "Je";

        var expectedRecipientsPage1 = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JEFFERSON, TestRecipient.JENNIFER);
        var expectedRecipientsPage2 = addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.JESSICA);
        addRecipientService.addRecipients(brazilBankAccountId, TestRecipient.VIRGINIO, TestRecipient.PATRIZIO);

        LOGGER.debug("Fetching page 1 recipients for bank account ID: {}, recipientNamePrefix: {} and limit: {}",
                brazilBankAccountId, prefixRecipientName, PAGE_LIMIT_2);

        RecipientsDTO recipientsDTOPage1 = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
                .queryParam("recipientName", prefixRecipientName)
                .queryParam("limit", PAGE_LIMIT_2)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.getCode())
                    .body("recipients", hasSize(expectedRecipientsPage1.size()))
                    .body("count", equalTo(expectedRecipientsPage1.size()))
                    .body("lastEvaluatedKey.bankAccountId", equalTo(brazilBankAccountId.toString()))
                    .body("lastEvaluatedKey.recipientName", equalTo(TestRecipient.JENNIFER.getRecipientName()))
                    .extract()
                        .as(RecipientsDTO.class);

        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsPage1, recipientsDTOPage1.recipients());

        var cacheKeyRecipientsDTOPage1 = new RecipientsCacheKey(brazilBankAccountId, QueryParams.builder()
                .withRecipientName(prefixRecipientName)
                .withLimit(PAGE_LIMIT_2)
                .build())
                .getCacheKey();

        assertThat(redisCommands.get(cacheKeyRecipientsDTOPage1))
                .as("Cached Page 1 RecipientsDTO should not be null")
                .isNotNull()
                .satisfies(cachedRecipientsDTO -> recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsPage1, cachedRecipientsDTO.recipients()));

        LOGGER.debug("Fetching page 2 recipients for bank account ID: {}, recipientNamePrefix: {}, limit: {} and last evaluated key: {}",
                brazilBankAccountId, prefixRecipientName, PAGE_LIMIT_2, recipientsDTOPage1.lastEvaluatedKey());

        var recipientsDTOPage2 = given()
            .spec(requestSpecification)
                .pathParam("bank-account-id", brazilBankAccountId)
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

        var cacheKeyRecipientsDTOPage2 = new RecipientsCacheKey(brazilBankAccountId, QueryParams.builder()
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