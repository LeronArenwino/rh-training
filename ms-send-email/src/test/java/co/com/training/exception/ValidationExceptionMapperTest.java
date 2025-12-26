package co.com.training.exception;

import co.com.training.model.response.Body;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationExceptionMapper.
 * 
 * <p>Tests verify that ConstraintViolationException exceptions are properly
 * mapped to standardized error responses with HTTP status 400 (Bad Request).
 * The tests cover scenarios with single and multiple constraint violations,
 * ensuring that all error messages are correctly collected and combined.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@QuarkusTest
@DisplayName("ValidationExceptionMapper Tests")
class ValidationExceptionMapperTest {

    private ValidationExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ValidationExceptionMapper();
    }

    @Test
    @DisplayName("Should return 400 status code for single constraint violation")
    void testToResponse_WithSingleViolation_ReturnsBadRequest() {
        // Given
        String errorMessage = "Email identifier cannot be null";
        ConstraintViolation<?> violation = createMockViolation(errorMessage);
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        Response response = mapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Should return 400 status code for multiple constraint violations")
    void testToResponse_WithMultipleViolations_ReturnsBadRequest() {
        // Given
        ConstraintViolation<?> violation1 = createMockViolation("Email identifier cannot be null");
        ConstraintViolation<?> violation2 = createMockViolation("Email recipient address cannot be null");
        ConstraintViolation<?> violation3 = createMockViolation("Email subject cannot be null");
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2, violation3);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        Response response = mapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Should combine single violation message correctly")
    void testToResponse_WithSingleViolation_CombinesMessage() {
        // Given
        String errorMessage = "Email recipient address must be a valid email address";
        ConstraintViolation<?> violation = createMockViolation(errorMessage);
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals(errorMessage, dataResponse.body().detail());
    }

    @Test
    @DisplayName("Should combine multiple violation messages with comma separator")
    void testToResponse_WithMultipleViolations_CombinesMessagesWithComma() {
        // Given
        String message1 = "Email identifier cannot be null";
        String message2 = "Email recipient address cannot be null";
        String message3 = "Email subject cannot be null";
        
        ConstraintViolation<?> violation1 = createMockViolation(message1);
        ConstraintViolation<?> violation2 = createMockViolation(message2);
        ConstraintViolation<?> violation3 = createMockViolation(message3);
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2, violation3);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        String detail = dataResponse.body().detail();
        // Verify all messages are present (order may vary due to Set)
        assertTrue(detail.contains(message1));
        assertTrue(detail.contains(message2));
        assertTrue(detail.contains(message3));
        // Verify comma separator is used
        assertTrue(detail.contains(", "));
        // Verify format: messages separated by ", "
        String[] parts = detail.split(", ");
        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("Should return correct response structure with Header")
    void testToResponse_ReturnsCorrectHeader() {
        // Given
        String errorMessage = "Email identifier cannot be null";
        ConstraintViolation<?> violation = createMockViolation(errorMessage);
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

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
        String errorMessage = "Email recipient address must be a valid email address";
        ConstraintViolation<?> violation = createMockViolation(errorMessage);
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

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
        String message1 = "Email identifier cannot be null";
        String message2 = "Email recipient address cannot be null";
        ConstraintViolation<?> violation1 = createMockViolation(message1);
        ConstraintViolation<?> violation2 = createMockViolation(message2);
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

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
        String detail = body.detail();
        // Verify all messages are present (order may vary due to Set)
        assertTrue(detail.contains(message1));
        assertTrue(detail.contains(message2));
        // Verify comma separator is used
        assertTrue(detail.contains(", "));
        // Verify format: messages separated by ", "
        String[] parts = detail.split(", ");
        assertEquals(2, parts.length);
    }

    @Test
    @DisplayName("Should handle empty constraint violations set")
    void testToResponse_WithEmptyViolations_ReturnsEmptyMessage() {
        // Given
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        assertEquals("", dataResponse.body().detail());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @DisplayName("Should handle violations with empty messages")
    void testToResponse_WithEmptyMessages_HandlesCorrectly() {
        // Given
        ConstraintViolation<?> violation1 = createMockViolation("");
        ConstraintViolation<?> violation2 = createMockViolation("Valid message");
        ConstraintViolation<?> violation3 = createMockViolation("");
        // Note: Set.of() will deduplicate, so we use HashSet to allow duplicates
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation1);
        violations.add(violation2);
        violations.add(violation3);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        String detail = dataResponse.body().detail();
        // Verify all messages are present (order may vary due to Set)
        assertTrue(detail.contains("Valid message"));
        // Verify comma separator is used
        assertTrue(detail.contains(", "));
        // Verify format: messages separated by ", "
        // Note: Set may deduplicate empty strings, so we check for at least 2 parts
        String[] parts = detail.split(", ");
        assertTrue(parts.length >= 2);
    }

    @Test
    @DisplayName("Should handle violations with null messages")
    void testToResponse_WithNullMessages_HandlesCorrectly() {
        // Given
        ConstraintViolation<?> violation1 = createMockViolation(null);
        ConstraintViolation<?> violation2 = createMockViolation("Valid message");
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        String detail = dataResponse.body().detail();
        // Verify all messages are present (order may vary due to Set)
        assertTrue(detail.contains("null"));
        assertTrue(detail.contains("Valid message"));
        // Verify comma separator is used
        assertTrue(detail.contains(", "));
        // Verify format: messages separated by ", "
        String[] parts = detail.split(", ");
        assertEquals(2, parts.length);
    }

    @Test
    @DisplayName("Should handle many constraint violations")
    void testToResponse_WithManyViolations_CombinesAllMessages() {
        // Given
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            violations.add(createMockViolation("Error " + i));
        }
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        Response response = mapper.toResponse(exception);
        DataResponse dataResponse = (DataResponse) response.getEntity();

        // Then
        assertNotNull(dataResponse);
        assertNotNull(dataResponse.body());
        String detail = dataResponse.body().detail();
        assertNotNull(detail);
        assertTrue(detail.contains("Error 1"));
        assertTrue(detail.contains("Error 10"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    /**
     * Helper method to create a ConstraintViolation with the specified message.
     * 
     * @param message The error message for the violation
     * @return A ConstraintViolation instance
     */
    private ConstraintViolation<?> createMockViolation(String message) {
        return new SimpleConstraintViolation(message);
    }

    /**
     * Simple implementation of ConstraintViolation for testing purposes.
     */
    private static class SimpleConstraintViolation implements ConstraintViolation<Object> {
        private final String message;

        public SimpleConstraintViolation(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return message != null ? "{" + message + "}" : null;
        }

        @Override
        public Object getRootBean() {
            return null;
        }

        @Override
        public Class<Object> getRootBeanClass() {
            return Object.class;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return null;
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> type) {
            return null;
        }
    }
}

