package com.blitzar.bank_account_recipient.security;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
@Schema(description = "Response object for successful authentication")
public record AuthenticationResponseDTO(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String access_token,

        @Schema(description = "Type of token", example = "Bearer")
        String token_type,

        @Schema(description = "Expiration time of the token in seconds", example = "3600")
        int expires_in,

        @Schema(description = "Authenticated username", example = "sherlock")
        String username
) {
        public String buildAuthorizationHeader() {
                return token_type + " " + access_token;
        }
}
