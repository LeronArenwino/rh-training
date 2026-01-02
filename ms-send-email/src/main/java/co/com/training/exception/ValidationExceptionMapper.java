package co.com.training.exception;

import co.com.training.model.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;

/**
 * Exception mapper for handling Bean Validation constraint violations.
 * 
 * <p>This mapper handles {@link ConstraintViolationException} exceptions that occur
 * when request validation fails. It collects all validation error messages and
 * returns a standardized error response with HTTP status 400 (Bad Request).</p>
 * 
 * <p>The mapper extracts all constraint violation messages and combines them into
 * a single error message, which is then formatted using {@link ErrorResponse}
 * to ensure consistency with other error responses.</p>
 * 
 * <p>This mapper is automatically registered by JAX-RS due to the {@link Provider}
 * annotation and will be invoked whenever a validation constraint is violated.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    /**
     * Maps a ConstraintViolationException to a standardized error response.
     * 
     * <p>Collects all constraint violation messages and combines them into
     * a single comma-separated error message.</p>
     * 
     * @param exception The constraint violation exception containing all
     *                  validation errors
     * @return A Response with status 400 (Bad Request) and a DataResponse
     *         containing the validation error messages
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String errorMessage = exception.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        
        return ErrorResponse.createValidationErrorResponse(errorMessage);
    }
}