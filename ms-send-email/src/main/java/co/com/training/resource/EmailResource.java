package co.com.training.resource;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import co.com.training.service.EmailService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource for email operations.
 * 
 * <p>This resource provides the REST API endpoint for resending emails.
 * It handles HTTP requests, validates the input, and delegates the business
 * logic to the {@link EmailService}.</p>
 * 
 * <p>The resource:
 * <ul>
 *   <li>Accepts JSON requests at {@code POST /api/v1/resend}</li>
 *   <li>Validates the request using Bean Validation annotations</li>
 *   <li>Delegates processing to the email service</li>
 *   <li>Returns standardized JSON responses with appropriate HTTP status codes</li>
 * </ul>
 * </p>
 * 
 * <p>Validation errors are automatically handled by exception mappers,
 * which return standardized error responses with null header and body
 * fields as specified in the requirements.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.1
 */
@Path("/api/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmailResource {
    
    private static final Logger LOG = Logger.getLogger(EmailResource.class);
    
    /**
     * Injected email service for handling business logic.
     */
    private final EmailService emailService;

    /**
     * Constructs a new EmailResource with the provided email service.
     * 
     * @param emailService The email service implementation to use
     */
    @Inject
    public EmailResource(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Resends an email through the SOAP service.
     * 
     * <p>This endpoint accepts a POST request with an {@link EmailRequest} in the body
     * and processes it to resend the email. The request is validated before processing,
     * and validation errors are handled by exception mappers.</p>
     * 
     * <p>Request validation includes:
     * <ul>
     *   <li>emailId must not be null</li>
     *   <li>recipient must not be null and must be a valid email address</li>
     *   <li>subject must not be null</li>
     *   <li>message must not be null</li>
     * </ul>
     * </p>
 
     * <p>The response HTTP status code is mapped from the DataResponse header codeResponse:
     * <ul>
     *   <li>200 - Success</li>
     *   <li>400/409 - Business error from SOAP service</li>
     *   <li>500 - Technical error</li>
     * </ul>
     * </p>
     * 
     * @param emailRequest The email request containing all necessary information
     *                     to resend the email. Must be valid according to Bean Validation rules.
     * @return A {@link Uni} that emits a {@link Response} with the appropriate HTTP status code
     *         and a {@link DataResponse} entity containing the result of the operation.
     *         The response structure includes:
     *         <ul>
     *           <li>header: Contains response code and message</li>
     *           <li>body: Contains email details (emailId, recipient, status, detail)</li>
     *         </ul>
     * @throws IllegalArgumentException if the request body is null
     */
    @POST
    @Path("/resend")
    public Uni<Response> resendEmail(@RequestBody(required = true) @Valid EmailRequest emailRequest) {
        // Validation will be handled by @Valid annotation and ExceptionMapper
        // If emailRequest is null, it will be caught by NullPointerExceptionMapper
        if (emailRequest == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        LOG.info("Resending email to: " + emailRequest.recipient());
        return emailService.sendEmail(emailRequest)
            .map(dataResponse -> {
                Header header = dataResponse.header();
                int httpStatus = mapCodeResponseToHttpStatus(header.codeResponse());
                return Response.status(httpStatus)
                    .entity(dataResponse)
                    .build();
            });
    }

    /**
     * Maps the internal codeResponse to the appropriate HTTP status code.
     * 
     * <p>This method ensures that:
     * <ul>
     *   <li>200 -> HTTP 200 (Success)</li>
     *   <li>400 -> HTTP 400 (Bad Request - Business error)</li>
     *   <li>409 -> HTTP 409 (Conflict - Business error)</li>
     *   <li>500 -> HTTP 500 (Internal Server Error - Technical error)</li>
     *   <li>Other codes -> HTTP 500 (Default for technical errors)</li>
     * </ul>
     * </p>
     * 
     * @param codeResponse The internal response code from the DataResponse header
     * @return The corresponding HTTP status code
     */
    private int mapCodeResponseToHttpStatus(Integer codeResponse) {
        if (codeResponse == null) {
            return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
        
        // Map business error codes (400, 409) and technical errors (500)
        if (codeResponse == 200) {
            return Response.Status.OK.getStatusCode();
        } else if (codeResponse == 400 || codeResponse == 409) {
            return codeResponse; // Return as-is for business errors
        } else {
            // Default to 500 for technical errors or unknown codes
            return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
    }
}
