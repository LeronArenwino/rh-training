package co.com.training.model.request;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record EmailRequest( 
    /*
     * Email identifier
     * @param emailId The unique identifier of the email
     */
    @Schema(description = "Email identifier", example = "1", required = true)
    String emailId,
    /*
     * Email recipient address
     * @param recipient The recipient email address
     */
    @Schema(description = "Email recipient address", example = "developer@training.com", required = true)
    String recipient,
    /*
     * Email subject
     * @param subject The subject of the email
     */
    @Schema(description = "Email subject", example = "Test email", required = true)
    String subject,
    /*
     * Email message
     * @param message The message of the email
     */
    @Schema(description = "Email message", example = "This is a test email", required = true)
    String message,
    /*
     * Email attacheds
     * @param emailAttacheds The attacheds of the email
     */
    @Schema(description = "Email attacheds", example = "This is a test email", required = true)
    List<EmailAttached> emailAttacheds
) {
}