package com.blitzar.bank_account_recipient.web.controller;

import com.blitzar.bank_account_recipient.service.AddRecipientService;
import com.blitzar.bank_account_recipient.service.dto.RecipientDTO;
import com.blitzar.bank_account_recipient.service.request.AddRecipientRequest;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Validated
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller(RecipientAPIConstants.RECIPIENTS_BASE_PATH_API_V1_MAPPING)
public class AddRecipientController {

    private static final Logger logger = LoggerFactory.getLogger(AddRecipientController.class);

    private final AddRecipientService addRecipientService;

    @Inject
    private final Validator validator;

    public AddRecipientController(AddRecipientService addRecipientService, Validator validator) {
        this.addRecipientService = addRecipientService;
        this.validator = validator;
    }

    @Status(HttpStatus.CREATED)
    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new recipient",
            description = "Creates a new recipient for the specified bank account. "
                    + "The request body includes the bankAccountId, recipientName, and IBAN. "
                    + "This endpoint returns the created recipient's data along with the resource location.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The request body containing the bankAccountId, recipientName, and IBAN.",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AddRecipientRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipient successfully created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RecipientDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid bank account ID, recipient name, or IBAN. Ensure that all required fields are valid."),
            @ApiResponse(responseCode = "500", description = "Internal server error. This may occur due to system issues, failed database connections, or unexpected runtime exceptions.")
    })
    public HttpResponse<RecipientDTO> addRecipient(@Body AddRecipientRequest addRecipientRequest, HttpRequest<?> request) {

        logger.info("[BankAccountId={}, RecipientName={}, IBAN={}] Incoming request to add recipient",
                addRecipientRequest.bankAccountId(), addRecipientRequest.recipientName(), addRecipientRequest.recipientIban());

        var recipientDTO = addRecipientService.addRecipient(addRecipientRequest);
        var locationUri = buildLocationUri(addRecipientRequest.bankAccountId());

        return HttpResponse.created(recipientDTO, locationUri);
    }

    private URI buildLocationUri(UUID bankAccountId) {
        return UriBuilder.of(RecipientAPIConstants.BANK_ACCOUNT_API_V1_MAPPING)
                .expand(Map.of("bank-account-id", bankAccountId));
    }
}
