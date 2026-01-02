package co.com.training.exception;

import co.com.training.model.response.Body;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NullPointerExceptionMapper.
 * 
 * <p>Tests verify that NullPointerException exceptions are properly
 * mapped to standardized error responses with HTTP status 400 (Bad Request).
 * The tests cover scenarios with different exception messages, including
 * cases where the message contains "emailRequest" for more specific error handling.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@QuarkusTest
@DisplayName("NullPointerExceptionMapper Tests")
class NullPointerExceptionMapperTest {

    private NullPointerExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NullPointerExceptionMapper();
    }

    @ParameterizedTest
    @DisplayName("Should return 400 status code for NullPointerException")
    @NullSource
    @ValueSource(strings = {
        "Cannot invoke method on null object",
        "Cannot access field on null object",
        "Object reference is null",
        "emailRequest cannot be null",
        ""
    })
    void testToResponse_ReturnsBadRequest(String errorMessage) {
        // Given
        NullPointerException exception = new NullPointerException(errorMessage);

        // When
        Response response = mapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @ParameterizedTest
    @DisplayName("Should return correct error message based on exception message")
    @MethodSource("errorMessageProvider")
    void testToResponse_ReturnsCorrectErrorMessage(String exceptionMessage, String expectedDetail) {
        // Given
        NullPointerException exception = new NullPointerException(exceptionMessage);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals(expectedDetail, dataResponse.body().detail());
    }

    static Stream<Arguments> errorMessageProvider() {
        return Stream.of(
            Arguments.of("emailRequest cannot be null", "Request body is required and cannot be null"),
            Arguments.of("emailRequest is null", "Request body is required and cannot be null"),
            Arguments.of("emailRequest parameter is null", "Request body is required and cannot be null"),
            Arguments.of("The emailRequest parameter cannot be null", "Request body is required and cannot be null"),
            Arguments.of("emailRequestField is null", "Request body is required and cannot be null"),
            Arguments.of("Cannot access field on null object", "Request body cannot be null or empty"),
            Arguments.of("Object reference is null", "Request body cannot be null or empty"),
            Arguments.of("EmailRequest is null", "Request body cannot be null or empty"),
            Arguments.of("", "Request body cannot be null or empty"),
            Arguments.of(null, "Request body cannot be null or empty")
        );
    }

    @Test
    @DisplayName("Should return correct response structure with Header")
    void testToResponse_ReturnsCorrectHeader() {
        // Given
        String errorMessage = "emailRequest is null";
        NullPointerException exception = new NullPointerException(errorMessage);

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
        String errorMessage = "emailRequest parameter is null";
        NullPointerException exception = new NullPointerException(errorMessage);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertNull(dataResponse.body().emailId());
        assertNull(dataResponse.body().recipient());
        assertEquals("ERROR", dataResponse.body().status());
        assertEquals("Request body is required and cannot be null", dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should return complete response structure")
    void testToResponse_ReturnsCompleteResponseStructure() {
        // Given
        String errorMessage = "emailRequest cannot be null";
        NullPointerException exception = new NullPointerException(errorMessage);

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
        assertEquals("Request body is required and cannot be null", body.detail());
    }

}

