package co.com.training.model.response;

import jakarta.ws.rs.core.Response;

public class ErrorResponse {

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
