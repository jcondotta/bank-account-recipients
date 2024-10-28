package com.jcondotta.recipients.factory.redis;

import com.jcondotta.recipients.service.dto.RecipientsDTO;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Factory;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;

@Factory
public class RedisCommandsFactory {

    @Singleton
    RedisRecipientsDTOCodec redisRecipientsDTOCodec(JsonMapper jsonMapper){
        return new RedisRecipientsDTOCodec(jsonMapper);
    }

    @Singleton
    public RedisCommands<String, RecipientsDTO> redisCommands(RedisClient redisClient, RedisRecipientsDTOCodec redisRecipientsDTOCodec) {
        StatefulRedisConnection<String, RecipientsDTO> connection = redisClient.connect(redisRecipientsDTOCodec);
        return connection.sync();
    }
}
