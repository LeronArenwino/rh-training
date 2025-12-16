package co.com.training.service;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import co.com.training.model.response.Body;
import com.training.services.MailServiceSoap;
import com.training.services.ResendEmailRequest;
import com.training.services.ResendEmailResult;
import com.training.services.ArrayOfAttachment;
import com.training.services.Attachment;
import io.quarkiverse.cxf.annotation.CXFClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link EmailService} interface.
 * 
 * <p>This service implementation handles the business logic for resending emails
 * through a SOAP service. It is responsible for:
 * <ul>
 *   <li>Converting internal request models to SOAP service models</li>
 *   <li>Invoking the SOAP service using the injected CXF client</li>
 *   <li>Mapping SOAP responses to internal response models</li>
 *   <li>Handling both business and technical errors</li>
 * </ul>
 * </p>
 * 
 * <p>The service uses Apache CXF for SOAP communication, which is injected
 * via the {@link io.quarkiverse.cxf.annotation.CXFClient} annotation.</p>
 * 
 * <p>All operations are executed asynchronously using Mutiny's {@link Uni}
 * to support reactive programming patterns.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.1
 */
@ApplicationScoped
public class EmailServiceImpl implements EmailService{

    private static final Logger LOG = Logger.getLogger(EmailServiceImpl.class);

    /**
     * Injected SOAP client for the mail service.
     * 
     * <p>This client is configured via application.properties and is used to
     * invoke the SOAP service operations defined in the WSDL.</p>
     */
    @Inject
    @CXFClient("mailService")
    MailServiceSoap mailServiceSoap;

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation:
     * <ol>
     *   <li>Converts {@link EmailRequest} to {@link ResendEmailRequest}</li>
     *   <li>Maps attachments if present</li>
     *   <li>Invokes the SOAP service</li>
     *   <li>Maps the SOAP response to {@link DataResponse}</li>
     *   <li>Handles exceptions and returns appropriate error responses</li>
     * </ol>
     * </p>
     */
    @Override
    public Uni<DataResponse> sendEmail(EmailRequest emailRequest) {
        return Uni.createFrom().item(() -> {
            try {
                // Convert EmailRequest to ResendEmailRequest of SOAP
                ResendEmailRequest soapRequest = new ResendEmailRequest();
                soapRequest.setEmailId(emailRequest.emailId());
                soapRequest.setRecipient(emailRequest.recipient());
                soapRequest.setSubject(emailRequest.subject());
                soapRequest.setMessage(emailRequest.message());
                
                // Convert attachments if they exist
                if (emailRequest.emailAttacheds() != null && !emailRequest.emailAttacheds().isEmpty()) {
                    ArrayOfAttachment attachments = new ArrayOfAttachment();
                    emailRequest.emailAttacheds().forEach(attached -> {
                        Attachment attachment = new Attachment();
                        attachment.setName(attached.attachedName());
                        attachment.setContent(attached.attachedPath()); // Adjust according to your logic
                        attachment.setContentType("application/octet-stream");
                        attachments.getAttachment().add(attachment);
                    });
                    soapRequest.setAttachments(attachments);
                }

                // Call the SOAP service
                ResendEmailResult soapResult = mailServiceSoap.resendEmail(soapRequest);

                // Convert SOAP response to DataResponse
                return mapSoapResponseToDataResponse(emailRequest, soapResult);
                
            } catch (Exception e) {
                LOG.error("Error calling SOAP service", e);
                // Handle technical errors - timeout, connection failure, 500 from provider
                String errorDetail = "Technical error consuming the WSDL";
                if (e.getMessage() != null) {
                    errorDetail += ": " + e.getMessage();
                } else {
                    errorDetail += " (timeout, connection failure, 500 from provider)";
                }
                return createErrorResponse(emailRequest, 500, "Internal Server Error", 
                    errorDetail);
            }
        });
    }

    /**
     * Maps a SOAP service response to the internal DataResponse model.
     * 
     * <p>This method handles both successful and business error responses from
     * the SOAP service. It extracts the relevant information and constructs
     * the appropriate {@link DataResponse}.</p>
     * 
     * @param emailRequest The original email request for reference
     * @param soapResult The result from the SOAP service
     * @return A DataResponse with appropriate status code and message based on
     *         the SOAP result. Returns code 200 for success, or the error code
     *         from the SOAP service for business errors.
     */
    private DataResponse mapSoapResponseToDataResponse(EmailRequest emailRequest, ResendEmailResult soapResult) {
        if (soapResult.isSuccess()) {
            // Successful response
            Header header = new Header(200, "Sent");
            Body body = new Body(
                emailRequest.emailId(),
                emailRequest.recipient(),
                "REENVIADO",
                soapResult.getMessage() != null ? soapResult.getMessage() : "Email resent successfully to the SOAP service"
            );
            return new DataResponse(header, body);
        } else {
            // Business error
            int statusCode = soapResult.getErrorCode() != null ? 
                Integer.parseInt(soapResult.getErrorCode()) : 400;
            // Ensure business error codes are 400 or 409
            if (statusCode != 400 && statusCode != 409) {
                statusCode = 400; // Default to 400 for business errors
            }
            Header header = new Header(statusCode, "Error");
            Body body = new Body(
                emailRequest.emailId(),
                emailRequest.recipient(),
                "ERROR",
                soapResult.getMessage() != null ? soapResult.getMessage() : "Business error received from the SOAP service"
            );
            return new DataResponse(header, body);
        }
    }

    /**
     * Creates an error response for technical failures.
     * 
     * <p>This method is used when a technical error occurs (e.g., network issues,
     * SOAP service unavailable, etc.) and creates a standardized error response
     * with the provided error details.</p>
     * 
     * @param emailRequest The original email request for reference
     * @param code The HTTP status code (typically 500 for technical errors)
     * @param message The error message for the header
     * @param detail The detailed error description for the body
     * @return A DataResponse with error status and details
     */
    private DataResponse createErrorResponse(EmailRequest emailRequest, int code, String message, String detail) {
        Header header = new Header(code, message);
        Body body = new Body(
            emailRequest.emailId(),
            emailRequest.recipient(),
            "ERROR",
            detail
        );
        return new DataResponse(header, body);
    }
}
