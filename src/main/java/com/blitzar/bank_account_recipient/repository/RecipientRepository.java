package com.blitzar.bank_account_recipient.repository;

import com.blitzar.bank_account_recipient.domain.Recipient;
import io.micronaut.data.model.Sort;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

@MongoRepository
public interface RecipientRepository extends CrudRepository<Recipient, String> {

    Collection<Recipient> find(Long bankAccountId, Sort sort);
    Optional<Recipient> find(Long bankAccountId, String id);

}