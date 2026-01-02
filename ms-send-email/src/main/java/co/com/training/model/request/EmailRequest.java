package co.com.training.model.request;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the request payload for resending an email.
 * 
 * <p>This record contains all the necessary information to resend an email through
 * the SOAP service, including the email identifier, recipient, subject, message,
 * and optional attachments.</p>
 * 
 * <p>All fields except {@code emailAttacheds} are required and validated:
 * <ul>
 *   <li>{@code emailId} - Must not be null</li>
 *   <li>{@code recipient} - Must not be null and must be a valid email address</li>
 *   <li>{@code subject} - Must not be null</li>
 *   <li>{@code message} - Must not be null</li>
 *   <li>{@code emailAttacheds} - Optional list of attachments</li>
 * </ul>
 * </p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
public record EmailRequest( 
    /*
     * Email identifier
     * @param emailId The unique identifier of the email
     */
    @Schema(description = "Email identifier", example = "1", required = true)
    @NotNull(message = "Email identifier cannot be null")
    String emailId,
    /*
     * Email recipient address
     * @param recipient The recipient email address
     */
    @Schema(description = "Email recipient address", example = "developer@training.com", required = true)
    @NotNull(message = "Email recipient address cannot be null")
    @Email(message = "Email recipient address must be a valid email address")
    String recipient,
    /*
     * Email subject
     * @param subject The subject of the email
     */
    @Schema(description = "Email subject", example = "Test email", required = true)
    @NotNull(message = "Email subject cannot be null")
    String subject,
    /*
     * Email message
     * @param message The message of the email
     */
    @Schema(description = "Email message", example = "This is a test email", required = true)
    @NotNull(message = "Email message cannot be null")
    String message,
    /*
     * Email attacheds
     * @param emailAttacheds The attacheds of the email
     */
    @Schema(description = "Email attacheds", example = "This is a test email", required = false)
    List<EmailAttached> emailAttacheds
) {
}