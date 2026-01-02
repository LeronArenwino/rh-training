package co.com.training.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import co.com.training.model.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Exception mapper for handling JSON processing exceptions.
 * 
 * <p>This mapper handles {@link com.fasterxml.jackson.core.JsonProcessingException}
 * exceptions that occur when the request body contains invalid or malformed JSON.
 * It provides specific error messages based on the type of JSON processing error
 * and returns a standardized error response with HTTP status 400 (Bad Request).</p>
 * 
 * <p>The mapper handles different types of JSON processing errors:
 * <ul>
 *   <li>{@link com.fasterxml.jackson.databind.exc.MismatchedInputException} - 
 *       When the request body is missing or cannot be parsed</li>
 *   <li>{@link com.fasterxml.jackson.databind.exc.InvalidFormatException} - 
 *       When the JSON format is invalid or data types don't match</li>
 *   <li>General JsonProcessingException - For other JSON processing errors</li>
 * </ul>
 * </p>
 * 
 * <p>This mapper is automatically registered by JAX-RS due to the {@link Provider}
 * annotation and will be invoked whenever a JSON processing error occurs during
 * request deserialization.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonProcessingExceptionMapper implements ExceptionMapper<com.fasterxml.jackson.core.JsonProcessingException> {

    /**
     * Maps a JsonProcessingException to a standardized error response.
     * 
     * <p>Determines the appropriate error message based on the specific type
     * of JSON processing error that occurred.</p>
     * 
     * @param exception The JSON processing exception that occurred
     * @return A Response with status 400 (Bad Request) and a DataResponse
     *         containing an appropriate error message based on the exception type
     */
    @Override
    public Response toResponse(com.fasterxml.jackson.core.JsonProcessingException exception) {
        String errorMessage = "Invalid JSON format in request body";
        
        if (exception instanceof MismatchedInputException) {
            errorMessage = "Request body is required and must be valid JSON";
        } else if (exception instanceof InvalidFormatException) {
            errorMessage = "Invalid data format in request body";
        }
        
        return ErrorResponse.createValidationErrorResponse(errorMessage);
    }
}
