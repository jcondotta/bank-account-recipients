package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindRecipientRepositoryTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    @InjectMocks
    private FindRecipientRepository findRecipientRepository;

    @Mock
    private DynamoDbTable<Recipient> dynamoDbTable;

    @Mock
    private Recipient recipientMock;

    @Test
    void shouldReturnRecipient_whenRecipientExists() {
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(recipientMock);
        var recipient = findRecipientRepository.findRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        assertThat(recipient).isPresent();

        verify(dynamoDbTable).getItem(any(Key.class));
        verifyNoMoreInteractions(dynamoDbTable);
    }

    @Test
    void shouldReturnOptionalEmpty_whenRecipientNonExists() {
        when(dynamoDbTable.getItem(any(Key.class))).thenReturn(null);

        Optional<Recipient> recipient = findRecipientRepository.findRecipient(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
        assertThat(recipient).isNotPresent();

        verify(dynamoDbTable).getItem(any(Key.class));
        verifyNoMoreInteractions(dynamoDbTable);
    }

}