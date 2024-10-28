package com.jcondotta.recipients.factory.redis;

import com.jcondotta.recipients.exception.RecipientsDeserializationException;
import com.jcondotta.recipients.exception.RecipientsSerializationException;
import com.jcondotta.recipients.service.dto.RecipientsDTO;
import io.lettuce.core.codec.RedisCodec;
import io.micronaut.json.JsonMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RedisRecipientsDTOCodec implements RedisCodec<String, RecipientsDTO> {

    private final JsonMapper jsonMapper;

    public RedisRecipientsDTOCodec(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public String decodeKey(ByteBuffer byteBuffer) {
        return StandardCharsets.UTF_8.decode(byteBuffer).toString();
    }

    @Override
    public RecipientsDTO decodeValue(ByteBuffer byteBuffer) {
        byte[] array = new byte[byteBuffer.remaining()];
        byteBuffer.get(array);
        try {
            return jsonMapper.readValue(array, RecipientsDTO.class);
        }
        catch (IOException e) {
            throw new RecipientsDeserializationException("Failed to deserialize RecipientsDTO", e);
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return StandardCharsets.UTF_8.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(RecipientsDTO recipientsDTO) {
        try {
            byte[] bytes = jsonMapper.writeValueAsBytes(recipientsDTO);
            return ByteBuffer.wrap(bytes);
        }
        catch (IOException e) {
            throw new RecipientsSerializationException("Failed to serialize RecipientsDTO", e);
        }
    }
}
