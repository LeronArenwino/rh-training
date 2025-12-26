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
     * SOAP client for the mail service.
     *
     * <p>This client is configured via application.properties and is used to
     * invoke the SOAP service operations defined in the WSDL.</p>
     */
    private final MailServiceSoap mailServiceSoap;

    /**
     * Constructor for dependency injection.
     *
     * @param mailServiceSoap the SOAP client for mail service
     */
    @Inject
    public EmailServiceImpl(@CXFClient("mailService") MailServiceSoap mailServiceSoap) {
        this.mailServiceSoap = mailServiceSoap;
    }

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
                ResendEmailRequest soapRequest = buildSoapRequest(emailRequest);
                ResendEmailResult soapResult = mailServiceSoap.resendEmail(soapRequest);
                return mapSoapResponseToDataResponse(emailRequest, soapResult);
            } catch (Exception e) {
                LOG.error("Error calling SOAP service", e);
                return handleTechnicalError(emailRequest, e);
            }
        });
    }

    /**
     * Builds a SOAP request from an EmailRequest.
     *
     * @param emailRequest The email request to convert
     * @return A ResendEmailRequest ready to be sent to the SOAP service
     */
    private ResendEmailRequest buildSoapRequest(EmailRequest emailRequest) {
        ResendEmailRequest soapRequest = new ResendEmailRequest();
        soapRequest.setEmailId(emailRequest.emailId());
        soapRequest.setRecipient(emailRequest.recipient());
        soapRequest.setSubject(emailRequest.subject());
        soapRequest.setMessage(emailRequest.message());

        if (hasAttachments(emailRequest)) {
            soapRequest.setAttachments(convertAttachments(emailRequest));
        }

        return soapRequest;
    }

    /**
     * Checks if the email request has attachments.
     *
     * @param emailRequest The email request to check
     * @return true if attachments exist, false otherwise
     */
    private boolean hasAttachments(EmailRequest emailRequest) {
        return emailRequest.emailAttacheds() != null && !emailRequest.emailAttacheds().isEmpty();
    }

    /**
     * Converts email attachments to SOAP attachments.
     *
     * @param emailRequest The email request containing attachments
     * @return An ArrayOfAttachment with converted attachments
     */
    private ArrayOfAttachment convertAttachments(EmailRequest emailRequest) {
        ArrayOfAttachment attachments = new ArrayOfAttachment();
        emailRequest.emailAttacheds().forEach(attached -> {
            Attachment attachment = createSoapAttachment(attached);
            attachments.getAttachment().add(attachment);
        });
        return attachments;
    }

    /**
     * Creates a SOAP attachment from an EmailAttached.
     *
     * @param attached The email attachment to convert
     * @return A SOAP Attachment object
     */
    private Attachment createSoapAttachment(co.com.training.model.request.EmailAttached attached) {
        Attachment attachment = new Attachment();
        attachment.setName(attached.attachedName());
        attachment.setContent(attached.attachedPath());
        attachment.setContentType("application/octet-stream");
        return attachment;
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
            return createSuccessResponse(emailRequest, soapResult);
        } else {
            return createBusinessErrorResponse(emailRequest, soapResult);
        }
    }

    /**
     * Creates a success response from a successful SOAP result.
     *
     * @param emailRequest The original email request
     * @param soapResult The successful SOAP result
     * @return A DataResponse with status 200
     */
    private DataResponse createSuccessResponse(EmailRequest emailRequest, ResendEmailResult soapResult) {
        Header header = new Header(200, "Sent");
        String message = soapResult.getMessage() != null 
            ? soapResult.getMessage() 
            : "Email resent successfully to the SOAP service";
        Body body = new Body(
            emailRequest.emailId(),
            emailRequest.recipient(),
            "REENVIADO",
            message
        );
        return new DataResponse(header, body);
    }

    /**
     * Creates a business error response from a failed SOAP result.
     *
     * @param emailRequest The original email request
     * @param soapResult The failed SOAP result
     * @return A DataResponse with appropriate business error code (400 or 409)
     */
    private DataResponse createBusinessErrorResponse(EmailRequest emailRequest, ResendEmailResult soapResult) {
        int statusCode = extractBusinessErrorCode(soapResult);
        Header header = new Header(statusCode, "Error");
        String message = soapResult.getMessage() != null 
            ? soapResult.getMessage() 
            : "Business error received from the SOAP service";
        Body body = new Body(
            emailRequest.emailId(),
            emailRequest.recipient(),
            "ERROR",
            message
        );
        return new DataResponse(header, body);
    }

    /**
     * Extracts and validates the business error code from a SOAP result.
     * Ensures the code is either 400 or 409, defaulting to 400 if invalid.
     *
     * @param soapResult The SOAP result containing the error code
     * @return A valid business error code (400 or 409)
     */
    private int extractBusinessErrorCode(ResendEmailResult soapResult) {
        int statusCode = soapResult.getErrorCode() != null 
            ? Integer.parseInt(soapResult.getErrorCode()) 
            : 400;
        
        if (statusCode != 400 && statusCode != 409) {
            statusCode = 400;
        }
        return statusCode;
    }

    /**
     * Handles technical errors that occur during SOAP service invocation.
     *
     * @param emailRequest The original email request
     * @param exception The exception that occurred
     * @return A DataResponse with status 500 and technical error details
     */
    private DataResponse handleTechnicalError(EmailRequest emailRequest, Exception exception) {
        String errorDetail = buildTechnicalErrorMessage(exception);
        return createErrorResponse(emailRequest, 500, "Internal Server Error", errorDetail);
    }

    /**
     * Builds a technical error message from an exception.
     *
     * @param exception The exception that occurred
     * @return A formatted error message
     */
    private String buildTechnicalErrorMessage(Exception exception) {
        String errorDetail = "Technical error consuming the WSDL";
        if (exception.getMessage() != null) {
            errorDetail += ": " + exception.getMessage();
        } else {
            errorDetail += " (timeout, connection failure, 500 from provider)";
        }
        return errorDetail;
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
