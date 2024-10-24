package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.exception.RecipientAlreadyExistsException;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddRecipientRepositoryTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    @InjectMocks
    private AddRecipientRepository addRecipientRepository;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Mock
    private Recipient recipient;

    @Test
    void shouldSaveRecipient_whenRecipientIsValid() {
        addRecipientRepository.add(recipient);

        var argumentCaptor = ArgumentCaptor.forClass(PutItemEnhancedRequest.class);
        verify(dynamoDbTable).putItem(argumentCaptor.capture());

        var argumentCaptorValue = argumentCaptor.getValue();
        assertThat(argumentCaptorValue.item()).isEqualTo(recipient);

        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldThrowRecipientAlreadyExistsException_whenSavingExistingRecipient() {
        doThrow(ConditionalCheckFailedException.class)
                .when(dynamoDbTable).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());

        when(recipient.getBankAccountId()).thenReturn(BANK_ACCOUNT_ID_BRAZIL);
        when(recipient.getRecipientName()).thenReturn(RECIPIENT_NAME_JEFFERSON);

        var recipientAlreadyExistsException = assertThrows(RecipientAlreadyExistsException.class,
                () -> addRecipientRepository.add(recipient));

        assertThat(recipientAlreadyExistsException)
                .satisfies(exception -> {
                    assertThat(exception.getMessage()).isEqualTo("recipient.alreadyExists");
                    assertThat(exception.getBankAccountId()).isEqualTo(recipient.getBankAccountId());
                    assertThat(exception.getRecipientName()).isEqualTo(recipient.getRecipientName());
                });

        verify(dynamoDbTable).putItem(Mockito.<PutItemEnhancedRequest<Recipient>>any());
        verifyNoMoreInteractions(dynamoDbTable);
    }
}
