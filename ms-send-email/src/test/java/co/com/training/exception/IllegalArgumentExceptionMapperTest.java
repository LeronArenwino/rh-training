package co.com.training.exception;

import co.com.training.model.response.Body;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IllegalArgumentExceptionMapper.
 * 
 * <p>Tests verify that IllegalArgumentException exceptions are properly
 * mapped to standardized error responses with HTTP status 400 (Bad Request).</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@QuarkusTest
@DisplayName("IllegalArgumentExceptionMapper Tests")
class IllegalArgumentExceptionMapperTest {

    private IllegalArgumentExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IllegalArgumentExceptionMapper();
    }

    @Test
    @DisplayName("Should return 400 status code when exception has a message")
    void testToResponse_WithMessage_ReturnsBadRequest() {
        // Given
        String errorMessage = "Invalid email format";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        Response response = mapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Should return 400 status code when exception has no message")
    void testToResponse_WithoutMessage_ReturnsBadRequest() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException((String) null);

        // When
        Response response = mapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Should use exception message when available")
    void testToResponse_WithMessage_UsesExceptionMessage() {
        // Given
        String errorMessage = "Email address cannot be empty";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals(errorMessage, dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should use default message when exception message is null")
    void testToResponse_WithoutMessage_UsesDefaultMessage() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException((String) null);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals("Invalid request", dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return correct response structure with Header")
    void testToResponse_ReturnsCorrectHeader() {
        // Given
        String errorMessage = "Invalid parameter value";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.header());
        assertEquals(400, dataResponse.header().codeResponse());
        assertEquals("Validation Error", dataResponse.header().messageResponse());
    }

    @Test
    @DisplayName("Should return correct response structure with Body")
    void testToResponse_ReturnsCorrectBody() {
        // Given
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertNull(dataResponse.body().emailId());
        assertNull(dataResponse.body().recipient());
        assertEquals("ERROR", dataResponse.body().status());
        assertEquals(errorMessage, dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return complete response structure")
    void testToResponse_ReturnsCompleteResponseStructure() {
        // Given
        String errorMessage = "Request validation failed";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.header());
        assertNotNull(dataResponse.body());
        
        // Verify Header
        Header header = dataResponse.header();
        assertEquals(400, header.codeResponse());
        assertEquals("Validation Error", header.messageResponse());
        
        // Verify Body
        Body body = dataResponse.body();
        assertNull(body.emailId());
        assertNull(body.recipient());
        assertEquals("ERROR", body.status());
        assertEquals(errorMessage, body.detail());
    }

    @Test
    @DisplayName("Should handle empty string message")
    void testToResponse_WithEmptyStringMessage() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("");

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals("", dataResponse.body().detail());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}

