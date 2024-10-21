package com.jcondotta.recipients.helper;

public enum TestRecipient {

    JEFFERSON("Jefferson Condotta", "ES38 0128 3316 2321 6644 7417"),
    PATRIZIO("Patrizio Condotta", "IT93 Q030 0203 2801 7517 1887 19 3"),
    VIRGINIO("Virginio Condotta", "IT49 W030 0203 2801 1452 4628 85 7"),
    INDALECIO("Indalecio Condotta", "BR23 4642 4773 8848 9796 8151 332C 5"),
    JESSICA("Jessica Condotta", "BR48 7399 5739 4597 3669 8619 729E 5");

    private final String recipientName;
    private final String recipientIban;

    TestRecipient(String name, String iban) {
        this.recipientName = name;
        this.recipientIban = iban;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getRecipientIban() {
        return recipientIban;
    }

    @Override
    public String toString() {
        return "TestRecipient{" +
                "name='" + recipientName + '\'' +
                ", iban='" + recipientIban + '\'' +
                '}';
    }
}