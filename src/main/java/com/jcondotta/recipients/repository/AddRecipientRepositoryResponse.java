package com.jcondotta.recipients.repository;

import com.jcondotta.recipients.domain.Recipient;

public record AddRecipientRepositoryResponse(Recipient recipient, boolean isIdempotent) {

    public static Builder builder(Recipient recipient) {
        return new Builder(recipient);
    }

    public static class Builder {
        private final Recipient recipient;
        private boolean isIdempotent = false;

        private Builder(Recipient recipient) {
            this.recipient = recipient;
        }

        public Builder isIdempotent(boolean isIdempotent) {
            this.isIdempotent = isIdempotent;
            return this;
        }

        public AddRecipientRepositoryResponse build() {
            return new AddRecipientRepositoryResponse(recipient, isIdempotent);
        }
    }
}