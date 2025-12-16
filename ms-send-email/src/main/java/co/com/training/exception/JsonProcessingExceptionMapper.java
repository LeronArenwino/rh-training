package co.com.training.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import co.com.training.model.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonProcessingExceptionMapper implements ExceptionMapper<com.fasterxml.jackson.core.JsonProcessingException> {

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
