package com.blitzar.bank_account_recipient;

import io.micronaut.test.support.TestPropertyProvider;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Testcontainers
public interface MongoDBTestContainer extends TestPropertyProvider {

    String mongodbImageName = "mongo:7";

    MongoDBContainer MONGODB_CONTAINER = new MongoDBContainer(mongodbImageName)
            .withExposedPorts(27017)
            .withReuse(true);

    @Override
    default Map<String, String> getProperties() {
        Startables.deepStart(MONGODB_CONTAINER).join();

        return Stream.of(getMongoDBProperties())
                .flatMap(property -> property.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    default Map<String, String> getMongoDBProperties() {
        return Map.of(
                "MONGODB_HOST", MONGODB_CONTAINER.getHost(),
                "MONGODB_PORT", MONGODB_CONTAINER.getMappedPort(27017).toString());
    }
}


