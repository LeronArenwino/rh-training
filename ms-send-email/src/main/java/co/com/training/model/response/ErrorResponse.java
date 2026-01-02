package co.com.training.model.response;

import jakarta.ws.rs.core.Response;

/**
 * Utility class for creating standardized error responses.
 * 
 * <p>This class provides a centralized way to create validation error responses
 * with a consistent format. All error responses follow the same structure:
 * <ul>
 *   <li>Header with code 400 and message "Validation Error"</li>
 *   <li>Body with null emailId and recipient, status "ERROR", and the error message</li>
 * </ul>
 * </p>
 * 
 * <p>This class is used by all exception mappers to ensure consistent error
 * response formatting across the application.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
public class ErrorResponse {

    /**
     * Private constructor to prevent instantiation.
     * 
     * <p>This class is a utility class and should not be instantiated.
     * All methods are static and should be called directly on the class.</p>
     */
    private ErrorResponse() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a standardized validation error response.
     * 
     * <p>This method creates a BAD_REQUEST (400) response with a standardized
     * error format. The response includes:
     * <ul>
     *   <li>Header with code 400 and message "Validation Error"</li>
     *   <li>Body with null emailId and recipient, status "ERROR", and the provided error message</li>
     * </ul>
     * </p>
     * 
     * @param errorMessage The error message to include in the response body.
     *                     This message describes what validation failed.
     * @return A JAX-RS Response object with status 400 (BAD_REQUEST) and
     *         a DataResponse entity containing the error details
     */
    public static Response createValidationErrorResponse(String errorMessage) {
        Header header = new Header(400, "Validation Error");
        Body body = new Body(
            null,
            null,
            "ERROR",
            errorMessage
        );
        
        DataResponse response = new DataResponse(header, body);
        
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(response)
            .build();
    }
}
