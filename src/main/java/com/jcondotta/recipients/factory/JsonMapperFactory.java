package com.jcondotta.recipients.factory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Factory;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;

@Factory
public class JsonMapperFactory {

    @Singleton
    JsonMapper jsonMapper(){
        return JsonMapper.createDefault();
    }
}
