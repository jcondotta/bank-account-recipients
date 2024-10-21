package com.jcondotta.recipients.web.controller;

import com.jcondotta.recipients.service.AddRecipientService;
import com.jcondotta.recipients.service.dto.RecipientDTO;
import com.jcondotta.recipients.service.request.AddRecipientRequest;
import com.jcondotta.recipients.service.dto.ExistentRecipientDTO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Validated
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RecipientAPIUriBuilder.RECIPIENTS_BASE_PATH_API_V1_MAPPING)
public class AddRecipientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddRecipientController.class);

    private final AddRecipientService addRecipientService;

    @Inject
    public AddRecipientController(AddRecipientService addRecipientService) {
        this.addRecipientService = addRecipientService;
    }

    @Status(HttpStatus.CREATED)
    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new recipient",
            description = "Creates a new recipient for the specified bank account. "
                    + "The request body includes the bankAccountId, recipientName, and IBAN. "
                    + "This endpoint returns the created recipient's data along with the resource location.",
            requestBody = @RequestBody(
                    description = "The request body containing the bankAccountId, recipientName, and IBAN.",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AddRecipientRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipient successfully created.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RecipientDTO.class))),
            @ApiResponse(responseCode = "200", description = "Recipient already exists, returning the existing data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RecipientDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid bank account ID, recipient name, or IBAN. Ensure that all required fields are valid."),
            @ApiResponse(responseCode = "500", description = "Internal server error. This may occur due to system issues, failed database connections, or unexpected runtime exceptions.")
    })
    public HttpResponse<RecipientDTO> addRecipient(@Body AddRecipientRequest addRecipientRequest, HttpRequest<?> request) {

        LOGGER.info("[BankAccountId={}, RecipientName={}, IBAN={}] Incoming request to add recipient",
                addRecipientRequest.bankAccountId(), addRecipientRequest.recipientName(), addRecipientRequest.recipientIban());

        var recipientDTO = addRecipientService.addRecipient(addRecipientRequest);

        if (recipientDTO instanceof ExistentRecipientDTO) {
            LOGGER.info("[BankAccountId={}, RecipientName={}, IBAN={}] Returning existing recipient",
                    addRecipientRequest.bankAccountId(), addRecipientRequest.recipientName(), addRecipientRequest.recipientIban());

            return HttpResponse.ok(recipientDTO);
        }
        else {
            var locationUri = RecipientAPIUriBuilder.fetchRecipientsURI(recipientDTO.getBankAccountId());
            return HttpResponse.created(recipientDTO, locationUri);  // 201 Created for new recipients
        }
    }
}
