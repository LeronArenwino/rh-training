package co.com.training.exception;

import co.com.training.model.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

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
