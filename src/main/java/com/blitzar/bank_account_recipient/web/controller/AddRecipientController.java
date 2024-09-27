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

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Validated
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
                    + "This endpoint returns the created recipient's data along with the resource location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipient successfully created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RecipientDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid bank account ID, recipient name, or IBAN. Ensure that all required fields are valid."),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error. An unexpected error occurred while processing the request.")
    })
    public HttpResponse<RecipientDTO> addRecipient(
            @Schema(description = "The request body containing the bankAccountId, recipientName, and IBAN.", requiredMode = RequiredMode.REQUIRED)
            @Body AddRecipientRequest addRecipientRequest,
            HttpRequest<?> request) {

        logger.info("[BankAccountId={}, RecipientName={}, IBAN={}] Incoming request to add recipient",
                addRecipientRequest.bankAccountId(), addRecipientRequest.recipientName(), addRecipientRequest.recipientIban());

        var recipientDTO = addRecipientService.addRecipient(addRecipientRequest);
        var locationUri = buildLocationUri(addRecipientRequest.bankAccountId()).build();

        return HttpResponse.created(locationUri)
                .body(recipientDTO);
    }

    private UriBuilder buildLocationUri(UUID bankAccountId) {
        var location = String.format(RecipientAPIConstants.BANK_ACCOUNT_API_V1_PLACE_HOLDER, bankAccountId);
        return UriBuilder.of(location);
    }
}