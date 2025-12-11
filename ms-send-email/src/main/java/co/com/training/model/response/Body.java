package co.com.training.model.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Body", description = "Represents the body of the response with email details")
public record Body(
        /*
         * Email identifier
         * 
         * @param emailId The unique identifier of the email
         */
        @Schema(description = "Email identifier", example = "1", required = true)
        String emailId,
        /*
         * Email recipient address
         * 
         * @param recipient The recipient email address
         */
        @Schema(description = "Email recipient address", example = "developer@training.com", required = true)
        String recipient,
        /*
         * Email status
         * 
         * @param status The current status of the email
         */
        @Schema(description = "Email status", example = "RESENT", required = true)
        String status,
        /*
         * Detailed message about the operation
         * 
         * @param detail The detailed description of the operation result
         */
        @Schema(description = "Detailed message about the operation", example = "Email resent successfully to the SOAP service", required = true)
        String detail) {
}
