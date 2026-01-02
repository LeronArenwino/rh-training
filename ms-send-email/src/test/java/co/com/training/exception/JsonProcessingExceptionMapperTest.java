package co.com.training.exception;

import co.com.training.model.response.Body;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Helper classes to create test instances of Jackson exceptions.
 * These classes extend the protected constructors to allow test instantiation.
 */
class TestJsonProcessingExceptions {
    static class TestMismatchedInputException extends MismatchedInputException {
        public TestMismatchedInputException(String message) {
            super(null, message);
        }
    }

    static class TestInvalidFormatException extends InvalidFormatException {
        public TestInvalidFormatException(String message) {
            super(null, message, null, String.class);
        }
    }
}

/**
 * Unit tests for JsonProcessingExceptionMapper.
 * 
 * <p>Tests verify that JsonProcessingException exceptions are properly
 * mapped to standardized error responses with HTTP status 400 (Bad Request).
 * The tests cover different types of JSON processing errors including
 * MismatchedInputException, InvalidFormatException, and general JsonProcessingException.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@QuarkusTest
@DisplayName("JsonProcessingExceptionMapper Tests")
class JsonProcessingExceptionMapperTest {

    private JsonProcessingExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new JsonProcessingExceptionMapper();
    }

    @Test
    @DisplayName("Should return 400 status code for general JsonProcessingException")
    void testToResponse_GeneralJsonProcessingException_ReturnsBadRequest() {
        // Given
        JsonProcessingException exception = new JsonProcessingException("Invalid JSON") {};

        // When
        Response response = mapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Should return correct error message for general JsonProcessingException")
    void testToResponse_GeneralJsonProcessingException_ReturnsCorrectMessage() {
        // Given
        JsonProcessingException exception = new JsonProcessingException("Invalid JSON") {};

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals("Invalid JSON format in request body", dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return correct error message for MismatchedInputException")
    void testToResponse_MismatchedInputException_ReturnsCorrectMessage() {
        // Given
        MismatchedInputException exception = new TestJsonProcessingExceptions.TestMismatchedInputException("Request body is missing");

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals("Request body is required and must be valid JSON", dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return 400 status code for MismatchedInputException")
    void testToResponse_MismatchedInputException_ReturnsBadRequest() {
        // Given
        MismatchedInputException exception = new TestJsonProcessingExceptions.TestMismatchedInputException("Request body is missing");

        // When
        Response response = mapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Should return correct error message for InvalidFormatException")
    void testToResponse_InvalidFormatException_ReturnsCorrectMessage() {
        // Given
        // Note: InvalidFormatException extends MismatchedInputException, so it will be caught by the first condition
        InvalidFormatException exception = new TestJsonProcessingExceptions.TestInvalidFormatException("Invalid format");

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        // Since InvalidFormatException extends MismatchedInputException, it matches the first condition
        assertEquals("Request body is required and must be valid JSON", dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return 400 status code for InvalidFormatException")
    void testToResponse_InvalidFormatException_ReturnsBadRequest() {
        // Given
        InvalidFormatException exception = new TestJsonProcessingExceptions.TestInvalidFormatException("Invalid format");

        // When
        Response response = mapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Should return correct response structure with Header for general exception")
    void testToResponse_GeneralException_ReturnsCorrectHeader() {
        // Given
        JsonProcessingException exception = new JsonProcessingException("Invalid JSON") {};

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
    @DisplayName("Should return correct response structure with Body for general exception")
    void testToResponse_GeneralException_ReturnsCorrectBody() {
        // Given
        JsonProcessingException exception = new JsonProcessingException("Invalid JSON") {};

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertNull(dataResponse.body().emailId());
        assertNull(dataResponse.body().recipient());
        assertEquals("ERROR", dataResponse.body().status());
        assertEquals("Invalid JSON format in request body", dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return correct response structure with Header for MismatchedInputException")
    void testToResponse_MismatchedInputException_ReturnsCorrectHeader() {
        // Given
        MismatchedInputException exception = new TestJsonProcessingExceptions.TestMismatchedInputException("Request body is missing");

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
    @DisplayName("Should return correct response structure with Body for MismatchedInputException")
    void testToResponse_MismatchedInputException_ReturnsCorrectBody() {
        // Given
        MismatchedInputException exception = new TestJsonProcessingExceptions.TestMismatchedInputException("Request body is missing");

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals("ERROR", dataResponse.body().status());
        assertEquals("Request body is required and must be valid JSON", dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return correct response structure with Header for InvalidFormatException")
    void testToResponse_InvalidFormatException_ReturnsCorrectHeader() {
        // Given
        InvalidFormatException exception = new TestJsonProcessingExceptions.TestInvalidFormatException("Invalid format");

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
    @DisplayName("Should return correct response structure with Body for InvalidFormatException")
    void testToResponse_InvalidFormatException_ReturnsCorrectBody() {
        // Given
        // Note: InvalidFormatException extends MismatchedInputException, so it will be caught by the first condition
        InvalidFormatException exception = new TestJsonProcessingExceptions.TestInvalidFormatException("Invalid format");

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals("ERROR", dataResponse.body().status());
        // Since InvalidFormatException extends MismatchedInputException, it matches the first condition
        assertEquals("Request body is required and must be valid JSON", dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return complete response structure for general exception")
    void testToResponse_GeneralException_ReturnsCompleteResponseStructure() {
        // Given
        JsonProcessingException exception = new JsonProcessingException("Invalid JSON") {};

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
        assertEquals("Invalid JSON format in request body", body.detail());
    }

    @Test
    @DisplayName("Should handle exception with null message")
    void testToResponse_WithNullMessage_HandlesCorrectly() {
        // Given
        JsonProcessingException exception = new JsonProcessingException((String) null) {};

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals("Invalid JSON format in request body", dataResponse.body().detail());
    }
}

