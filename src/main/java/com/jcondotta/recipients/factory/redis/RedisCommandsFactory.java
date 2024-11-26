package com.jcondotta.recipients.factory.redis;

import com.jcondotta.recipients.service.cache.RedisRecipientsDTOCodec;
import io.micronaut.context.annotation.Factory;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Factory
public class RedisCommandsFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCommandsFactory.class);

    @Singleton
    RedisRecipientsDTOCodec redisRecipientsDTOCodec(JsonMapper jsonMapper){
        return new RedisRecipientsDTOCodec(jsonMapper);
    }
//
//    @Singleton
//    @Replaces(StatefulRedisConnection.class)
//    StatefulRedisConnection<String, RecipientsDTO> statefulRedisConnection(RedisClient redisClient, RedisRecipientsDTOCodec redisRecipientsDTOCodec) {
//        LOGGER.info("Establishing Stateful Redis connection with custom codec: {}", redisRecipientsDTOCodec.getClass().getSimpleName());
//        return redisClient.connect(redisRecipientsDTOCodec);
//    }

//    @Singleton
//    @Replaces(RedisCommands.class)
//    RedisCommands<String, RecipientsDTO> redisCommands(StatefulRedisConnection<String, RecipientsDTO> statefulRedisConnection) {
//        redisClient.connect()
//
//
//        var redisCommands = statefulRedisConnection.sync();
//        redisCommands.set("Jefferson", new RecipientsDTO(List.of(), 10, null));
//        var recipientsDTO = redisCommands.get("Jefferson");
//        LOGGER.info("Count: {}", recipientsDTO.count());
//        LOGGER.info("RecipientDTO", recipientsDTO.recipients());
//
//        return redisCommands;
//    }
}
