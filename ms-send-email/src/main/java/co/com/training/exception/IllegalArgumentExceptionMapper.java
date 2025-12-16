package co.com.training.exception;

import co.com.training.model.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper for handling IllegalArgumentException.
 * 
 * <p>This mapper handles {@link IllegalArgumentException} exceptions that occur
 * when invalid arguments are passed to methods. It extracts the exception message
 * and returns a standardized error response with HTTP status 400 (Bad Request).</p>
 * 
 * <p>This mapper is commonly used when the request body is explicitly validated
 * as null or when other argument validation fails at the resource level.</p>
 * 
 * <p>This mapper is automatically registered by JAX-RS due to the {@link Provider}
 * annotation and will be invoked whenever an IllegalArgumentException is thrown
 * during request processing.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    /**
     * Maps an IllegalArgumentException to a standardized error response.
     * 
     * <p>Uses the exception's message if available, otherwise provides a
     * default "Invalid request" message.</p>
     * 
     * @param exception The illegal argument exception that occurred
     * @return A Response with status 400 (Bad Request) and a DataResponse
     *         containing the error message from the exception
     */
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        String errorMessage = exception.getMessage() != null ? 
            exception.getMessage() : "Invalid request";
        
        return ErrorResponse.createValidationErrorResponse(errorMessage);
    }
}
