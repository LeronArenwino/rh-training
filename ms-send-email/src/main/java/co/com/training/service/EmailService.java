package co.com.training.service;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import io.smallrye.mutiny.Uni;

/**
 * Service interface for email operations.
 * 
 * <p>This interface defines the contract for email-related operations,
 * specifically for resending emails through a SOAP service. The implementation
 * handles the conversion between internal request models and SOAP service
 * models, as well as the conversion of SOAP responses to internal response models.</p>
 * 
 * <p>All operations return {@link Uni} to support reactive programming patterns
 * in Quarkus.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
public interface EmailService {
    
    /**
     * Sends an email by invoking the SOAP service.
     * 
     * <p>This method converts the internal {@link EmailRequest} to the SOAP
     * service's {@link com.training.services.ResendEmailRequest}, invokes the
     * SOAP service, and converts the response back to {@link DataResponse}.</p>
     * 
     * <p>The method handles:
     * <ul>
     *   <li>Conversion of request models</li>
     *   <li>Attachment mapping if present</li>
     *   <li>SOAP service invocation</li>
     *   <li>Response mapping (success or error)</li>
     *   <li>Exception handling for technical failures</li>
     * </ul>
     * </p>
     * 
     * @param emailRequest The email request containing all necessary information
     *                     to resend the email (id, recipient, subject, message, attachments)
     * @return A {@link Uni} that emits a {@link DataResponse} containing the
     *         result of the operation. The response includes:
     *         <ul>
     *           <li>Success case: code 200, status "Sent"</li>
     *           <li>Business error: code 400/409 from SOAP service, status "ERROR"</li>
     *           <li>Technical error: code 500, status "ERROR"</li>
     *         </ul>
     */
    Uni<DataResponse> sendEmail(EmailRequest emailRequest);

}
