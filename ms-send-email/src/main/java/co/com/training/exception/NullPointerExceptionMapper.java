package co.com.training.exception;

import co.com.training.model.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper for handling NullPointerException.
 * 
 * <p>This mapper handles {@link NullPointerException} exceptions that occur
 * when the request body is null or when attempting to access null objects.
 * It provides user-friendly error messages and returns a standardized error
 * response with HTTP status 400 (Bad Request).</p>
 * 
 * <p>The mapper specifically checks if the exception is related to the emailRequest
 * parameter to provide a more specific error message about the request body being null.</p>
 * 
 * <p>This mapper is automatically registered by JAX-RS due to the {@link Provider}
 * annotation and will be invoked whenever a NullPointerException occurs during
 * request processing.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

    /**
     * Maps a NullPointerException to a standardized error response.
     * 
     * <p>Determines the appropriate error message based on the exception context.
     * If the exception is related to the emailRequest parameter, it provides a
     * specific message about the request body being required.</p>
     * 
     * @param exception The null pointer exception that occurred
     * @return A Response with status 400 (Bad Request) and a DataResponse
     *         containing an appropriate error message
     */
    @Override
    public Response toResponse(NullPointerException exception) {
        // Check if the error is related to null request body
        String errorMessage = "Request body cannot be null or empty";
        
        if (exception.getMessage() != null && exception.getMessage().contains("emailRequest")) {
            errorMessage = "Request body is required and cannot be null";
        }
        
        return ErrorResponse.createValidationErrorResponse(errorMessage);
    }
}
