package co.com.training.exception;

import co.com.training.model.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        String errorMessage = exception.getMessage() != null ? 
            exception.getMessage() : "Invalid request";
        
        return ErrorResponse.createValidationErrorResponse(errorMessage);
    }
}
