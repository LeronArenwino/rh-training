package co.com.training.exception;

import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import co.com.training.model.response.Body;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        // Collect all error messages
        String errorMessage = exception.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

        // Create response with null header and body according to requirements
        Header header = new Header(Response.Status.BAD_REQUEST.getStatusCode(), "Validation Error");
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