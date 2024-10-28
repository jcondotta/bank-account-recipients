package com.jcondotta.recipients.service.cache;

import com.jcondotta.recipients.exception.RecipientsDeserializationException;
import com.jcondotta.recipients.exception.RecipientsSerializationException;
import com.jcondotta.recipients.factory.RecipientDTOTestFactory;
import com.jcondotta.recipients.helper.TestBankAccount;
import com.jcondotta.recipients.helper.TestRecipient;
import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import com.jcondotta.recipients.service.request.LastEvaluatedKey;
import com.jcondotta.recipients.service.request.QueryParams;
import io.micronaut.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisRecipientsDTOCodecTest {

    private static final UUID BANK_ACCOUNT_ID_BRAZIL = TestBankAccount.BRAZIL.getBankAccountId();
    private static final String RECIPIENT_NAME_JEFFERSON = TestRecipient.JEFFERSON.getRecipientName();

    private final JsonMapper jsonMapper = JsonMapper.createDefault();
    private final RedisRecipientsDTOCodec redisRecipientsDTOCodec = new RedisRecipientsDTOCodec(jsonMapper);

    private final QueryParams queryParams = QueryParams.builder().build();
    private final RecipientsCacheKey recipientsCacheKey = new RecipientsCacheKey(BANK_ACCOUNT_ID_BRAZIL, queryParams);

    private final RecipientDTO jeffersonRecipientDTO = RecipientDTOTestFactory.createRecipientDTO(BANK_ACCOUNT_ID_BRAZIL, TestRecipient.JEFFERSON);
    private final LastEvaluatedKey lastEvaluatedKey = new LastEvaluatedKey(BANK_ACCOUNT_ID_BRAZIL, RECIPIENT_NAME_JEFFERSON);
    private final RecipientsDTO recipientsDTO = new RecipientsDTO(List.of(jeffersonRecipientDTO), 1, lastEvaluatedKey);

    @Test
    void shouldDecodeKey_whenByteBufferContainsValidString() {
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(recipientsCacheKey.getCacheKey());
        String decodedKey = redisRecipientsDTOCodec.decodeKey(byteBuffer);
        assertEquals(recipientsCacheKey.getCacheKey(), decodedKey, "Decoded key should match the original key");
    }

    @Test
    void shouldEncodeKey_whenStringKeyIsValid() {
        ByteBuffer encodedKey = redisRecipientsDTOCodec.encodeKey(recipientsCacheKey.getCacheKey());
        String result = StandardCharsets.UTF_8.decode(encodedKey).toString();
        assertEquals(recipientsCacheKey.getCacheKey(), result, "Encoded key should decode back to the original key");
    }

    @Test
    void shouldDecodeValue_whenByteBufferContainsValidRecipientsDTO() throws IOException {
        byte[] recipientsDTOBytes = jsonMapper.writeValueAsBytes(recipientsDTO);
        ByteBuffer byteBuffer = ByteBuffer.wrap(recipientsDTOBytes);

        RecipientsDTO decodedRecipientsDTO = redisRecipientsDTOCodec.decodeValue(byteBuffer);
        assertThat(decodedRecipientsDTO)
                .as("Decoded value should match the expected RecipientsDTO")
                .usingRecursiveComparison()
                .isEqualTo(recipientsDTO);
    }

    @Test
    void shouldThrowRecipientsDeserializationException_whenDecodingInvalidValue() {
        var invalidRecipientDTOBytes = "invalidRecipientDTOBytes".getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.wrap(invalidRecipientDTOBytes);

        var exception = assertThrows(RecipientsDeserializationException.class, () -> redisRecipientsDTOCodec.decodeValue(byteBuffer));

        assertThat(exception.getMessage()).isEqualTo("Failed to deserialize RecipientsDTO");
        assertThat(exception.getCause()).isInstanceOf(IOException.class);
    }

    @Test
    void shouldEncodeValue_whenRecipientsDTOIsValid() throws IOException {
        byte[] recipientsDTOBytes = jsonMapper.writeValueAsBytes(recipientsDTO);
        ByteBuffer result = redisRecipientsDTOCodec.encodeValue(recipientsDTO);

        assertArrayEquals(recipientsDTOBytes, result.array(), "Encoded value should match the expected byte array");
    }

    @Test
    void shouldThrowRecipientsSerializationException_whenEncodingInvalidValue() throws IOException {
        var jsonMapperMock = mock(JsonMapper.class);
        var codec = new RedisRecipientsDTOCodec(jsonMapperMock);

        when(jsonMapperMock.writeValueAsBytes(argThat(argument -> argument instanceof RecipientsDTO)))
                .thenThrow(new IOException("Serialization error"));

        var exception = assertThrows(RecipientsSerializationException.class,
                () -> codec.encodeValue(recipientsDTO));

        assertTrue(exception.getMessage().contains("Failed to serialize RecipientsDTO"),
                "Exception message should indicate serialization failure");

        verify(jsonMapperMock).writeValueAsBytes(recipientsDTO);
    }
}
