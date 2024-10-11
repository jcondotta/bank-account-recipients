package com.blitzar.bank_account_recipient.service;

import com.blitzar.bank_account_recipient.domain.Recipient;
import com.blitzar.bank_account_recipient.factory.RecipientTestFactory;
import com.blitzar.bank_account_recipient.factory.ValidatorTestFactory;
import com.blitzar.bank_account_recipient.helper.TestBankAccount;
import com.blitzar.bank_account_recipient.helper.TestRecipient;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.request.LastEvaluatedKey;
import com.blitzar.bank_account_recipient.service.request.QueryParams;
import com.blitzar.bank_account_recipient.validation.recipient.RecipientsValidator;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchRecipientServiceTest {

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();
    private final RecipientsValidator recipientsValidator = new RecipientsValidator();

    private FetchRecipientService fetchRecipientService;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Mock
    private PageIterable<Recipient> pageIterable;

    @Mock
    private Page<Recipient> recipientPage;

    @Mock
    private QueryParams queryParams;


    @BeforeEach
    void beforeEach(){
        fetchRecipientService = new FetchRecipientService(dynamoDbTable, VALIDATOR);

        when(dynamoDbTable.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(recipientPage));
    }

    @Test
    void shouldReturnRecipientList_whenBankAccountIdProvidedWithoutQueryParams() {
        var recipients = RecipientTestFactory.createRecipients(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON, TestRecipient.JESSICA);

        when(recipientPage.items()).thenReturn(recipients);

        var recipientsDTO = fetchRecipientService.findRecipients(TestBankAccount.BRAZIL.getBankAccountId(), queryParams);

        assertAll(
                () -> assertThat(recipientsDTO.recipients()).hasSize(recipients.size()),
                () -> assertThat(recipientsDTO.count()).isEqualTo(recipients.size()),
                () -> assertThat(recipientsDTO.lastEvaluatedKey()).isNull()
        );

        var expectedRecipientsDTO = recipients.stream().map(RecipientDTO::new).toList();
        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsDTO, recipientsDTO.recipients());
    }

    @Test
    void shouldReturnRecipientListWithRecipientNamePrefix_whenRecipientNameIsProvided() {
        var recipients = RecipientTestFactory.createRecipients(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON, TestRecipient.PATRIZIO);

        final var prefixRecipientName = "Je";

        when(recipientPage.items()).thenReturn(recipients);
        when(queryParams.recipientName()).thenReturn(Optional.of(prefixRecipientName));

        var recipientsDTO = fetchRecipientService.findRecipients(TestBankAccount.BRAZIL.getBankAccountId(), queryParams);

        assertAll(
                () -> assertThat(recipientsDTO.recipients()).hasSize(recipients.size()),
                () -> assertThat(recipientsDTO.count()).isEqualTo(recipients.size()),
                () -> assertThat(recipientsDTO.lastEvaluatedKey()).isNull()
        );

        var expectedRecipientsDTO = recipients.stream().map(RecipientDTO::new).toList();
        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsDTO, recipientsDTO.recipients());
    }

    @Test
    void shouldReturnEmptyRecipientList_whenNonExistentBankAccountIdIsProvided() {
        when(recipientPage.items()).thenReturn(Collections.emptyList());

        var recipientsDTO = fetchRecipientService.findRecipients(TestBankAccount.ITALY.getBankAccountId(), queryParams);

        assertAll(
                () -> assertThat(recipientsDTO.recipients()).isEmpty(),
                () -> assertThat(recipientsDTO.count()).isZero(),
                () -> assertThat(recipientsDTO.lastEvaluatedKey()).isNull()
        );
    }

    @Test
    void shouldReturn204NoContent_whenFilteringByNonExistentPrefixRecipientName(){
        final var nonExistentPrefixRecipientName = "Z";

        when(recipientPage.items()).thenReturn(Collections.emptyList());
        when(queryParams.recipientName()).thenReturn(Optional.of(nonExistentPrefixRecipientName));

        var recipientsDTO = fetchRecipientService.findRecipients(TestBankAccount.BRAZIL.getBankAccountId(), queryParams);

        assertAll(
                () -> assertThat(recipientsDTO.recipients()).isEmpty(),
                () -> assertThat(recipientsDTO.count()).isZero(),
                () -> assertThat(recipientsDTO.lastEvaluatedKey()).isNull()
        );
    }

    @Test
    void shouldReturnPagedRecipientList_whenLimitIsProvided() {
        var recipientJefferson = RecipientTestFactory.createRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);
        var recipientPatrizio = RecipientTestFactory.createRecipient(TestBankAccount.BRAZIL, TestRecipient.PATRIZIO);

        var recipientsPage1 = List.of(recipientJefferson, recipientPatrizio);

        when(queryParams.limit()).thenReturn(Optional.of(recipientsPage1.size()));
        when(recipientPage.items()).thenReturn(recipientsPage1);
        when(recipientPage.lastEvaluatedKey())
                .thenReturn(Map.of(
                        "bankAccountId", AttributeValue.fromS(recipientPatrizio.getBankAccountId().toString()),
                        "recipientName", AttributeValue.fromS(recipientPatrizio.getRecipientName())
                ));

        var recipientsDTO = fetchRecipientService.findRecipients(TestBankAccount.BRAZIL.getBankAccountId(), queryParams);

        assertAll(
                () -> assertThat(recipientsDTO.recipients()).hasSize(recipientsPage1.size()),
                () -> assertThat(recipientsDTO.count()).isEqualTo(recipientsPage1.size()),
                () -> assertThat(recipientsDTO.lastEvaluatedKey().bankAccountId()).isEqualTo(recipientPatrizio.getBankAccountId()),
                () -> assertThat(recipientsDTO.lastEvaluatedKey().recipientName()).isEqualTo(recipientPatrizio.getRecipientName())
        );

        var expectedRecipientsDTO = recipientsPage1.stream().map(RecipientDTO::new).toList();
        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsDTO, recipientsDTO.recipients());
    }

    @Test
    void shouldReturnPagedRecipientList_whenLastEvaluatedKeyIsProvided() {
        var recipientJefferson = RecipientTestFactory.createRecipient(TestBankAccount.BRAZIL, TestRecipient.JEFFERSON);
        var recipientPatrizio = RecipientTestFactory.createRecipient(TestBankAccount.BRAZIL, TestRecipient.PATRIZIO);
        var recipientVirginio = RecipientTestFactory.createRecipient(TestBankAccount.BRAZIL, TestRecipient.VIRGINIO);

        var recipientsPage1 = List.of(recipientPatrizio, recipientVirginio);

        when(queryParams.limit()).thenReturn(Optional.of(2));
        when(queryParams.lastEvaluatedKey()).
                thenReturn(Optional.of(new LastEvaluatedKey(recipientJefferson.getBankAccountId(), recipientJefferson.getRecipientName())));

        when(recipientPage.items()).thenReturn(recipientsPage1);

        var recipientsDTO = fetchRecipientService.findRecipients(TestBankAccount.BRAZIL.getBankAccountId(), queryParams);

        assertAll(
                () -> assertThat(recipientsDTO.recipients()).hasSize(recipientsPage1.size()),
                () -> assertThat(recipientsDTO.count()).isEqualTo(recipientsPage1.size()),
                () -> assertThat(recipientsDTO.lastEvaluatedKey()).isNull()
        );

        var expectedRecipientsDTO = recipientsPage1.stream().map(RecipientDTO::new).toList();
        recipientsValidator.validateDTOsAgainstDTOs(expectedRecipientsDTO, recipientsDTO.recipients());
    }
}