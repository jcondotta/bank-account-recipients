package com.jcondotta.recipients.web;


import com.jcondotta.recipients.container.LocalStackTestContainer;
import com.jcondotta.recipients.helper.AddRecipientServiceFacade;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.DeleteRecipientService;
import com.jcondotta.recipients.service.FetchRecipientService;
import com.jcondotta.recipients.service.cache.RecipientsCacheKey;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.DeleteRecipientRequest;
import com.jcondotta.recipients.service.request.QueryParams;
import com.jcondotta.recipients.service.request.QueryRecipientsRequest;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class CacheEvictionIT implements LocalStackTestContainer {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final UUID BANK_ACCOUNT_ID_ITALY = TestBankAccount.ITALY.getBankAccountId();

    @Inject
    AddRecipientServiceFacade addRecipientService;

    @Inject
    FetchRecipientService fetchRecipientService;

    @Inject
    DeleteRecipientService deleteRecipientService;

    @Inject
    RedisCommands<String, RecipientsDTO> redisCommands;

    private final QueryParams queryParams = QueryParams.builder().build();

    @Test
    public void shouldEvictCacheEntries_whenAddRecipientRelatedToBankAccountId(){
        addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);
        addRecipientService.addRecipient(BANK_ACCOUNT_ID_ITALY, TestRecipient.VIRGINIO);

        Stream.of(BANK_ACCOUNT_ID_BRAZIL, BANK_ACCOUNT_ID_ITALY).forEach(bankAccountId -> {
            var queryRecipientsRequest = new QueryRecipientsRequest(bankAccountId, queryParams);
            fetchRecipientService.findRecipients(queryRecipientsRequest);

            var recipientsCacheKey = new RecipientsCacheKey(bankAccountId, queryParams);
            assertThat(redisCommands.get(recipientsCacheKey.getCacheKey()))
                    .as("Cache entry should exist for bankAccountId: " + bankAccountId)
                    .isNotNull();
        });

        addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);

        var recipientsCacheBrazil = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        assertThat(redisCommands.get(recipientsCacheBrazil.getCacheKey()))
                .as("Cache entry for Brazil should be invalidated")
                .isNull();

        var recipientsCacheItaly = new RecipientsCacheKey(BANK_ACCOUNT_ID_ITALY, queryParams);
        assertThat(redisCommands.get(recipientsCacheItaly.getCacheKey()))
                .as("Cache entry for Italy should still exist")
                .isNotNull();
    }

    @Test
    public void shouldEvictCacheEntries_whenDeleteRecipientRelatedToBankAccountId(){
        addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.PATRIZIO);
        addRecipientService.addRecipient(BANK_ACCOUNT_ID_ITALY, TestRecipient.VIRGINIO);

        Stream.of(BANK_ACCOUNT_ID_BRAZIL, BANK_ACCOUNT_ID_ITALY).forEach(bankAccountId -> {
            var queryRecipientsRequest = new QueryRecipientsRequest(bankAccountId, queryParams);
            fetchRecipientService.findRecipients(queryRecipientsRequest);

            var recipientsCacheKey = new RecipientsCacheKey(bankAccountId, queryParams);
            assertThat(redisCommands.get(recipientsCacheKey.getCacheKey()))
                    .as("Cache entry should exist for bankAccountId: " + bankAccountId)
                    .isNotNull();
        });

        deleteRecipientService.deleteRecipient(new DeleteRecipientRequest(
                BANK_ACCOUNT_ID_BRAZIL, TestRecipient.PATRIZIO.getRecipientName()));

        var recipientsCacheBrazil = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams);
        assertThat(redisCommands.get(recipientsCacheBrazil.getCacheKey()))
                .as("Cache entry for Brazil should be invalidated")
                .isNull();

        var recipientsCacheItaly = new RecipientsCacheKey(BANK_ACCOUNT_ID_ITALY, queryParams);
        assertThat(redisCommands.get(recipientsCacheItaly.getCacheKey()))
                .as("Cache entry for Italy should still exist")
                .isNotNull();
    }

    @Test
    public void shouldEvictCacheEntry_whenCacheIsExpired(){
        addRecipientService.addRecipients(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON, TestRecipient.PATRIZIO);

        fetchRecipientService.findRecipients(new QueryRecipientsRequest(BANK_ACCOUNT_ID_BRAZIL, queryParams));

        var cacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams).getCacheKey();
        assertThat(redisCommands.get(cacheKey))
                .as("Cache entry should initially be set for Brazil")
                .isNotNull();

        await().pollDelay(1, TimeUnit.SECONDS).atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            LOGGER.debug("Checking if cache entry expired for bank account ID: {}", BANK_ACCOUNT_ID_BRAZIL);

            assertThat(redisCommands.get(cacheKey))
                    .as("Cache entry should be expired")
                    .isNull();
        });
    }
}
