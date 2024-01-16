package com.blitzar.bank_account_recipient.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public class Recipient {

    @Id
    @GeneratedValue
    private String id;

    private String name;
    private String iban;
    private Long bankAccountId;

    public Recipient() {
    }

    public Recipient(String name, String iban, Long bankAccountId) {
        this.name = name;
        this.iban = iban;
        this.bankAccountId = bankAccountId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
