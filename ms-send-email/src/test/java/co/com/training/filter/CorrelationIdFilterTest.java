package co.com.training.filter;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * Unit tests for CorrelationIdFilter.
 * 
 * <p>Tests verify that the filter properly:
 * <ul>
 *   <li>Extracts correlation ID from request headers when present</li>
 *   <li>Generates new correlation IDs when not present</li>
 *   <li>Stores correlation ID in MDC for logging</li>
 *   <li>Adds correlation ID to response headers</li>
 *   <li>Cleans up MDC after request processing</li>
 * </ul>
 * </p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.1
 */
@QuarkusTest
@DisplayName("CorrelationIdFilter Tests")
class CorrelationIdFilterTest {

    private CorrelationIdFilter filter;
    private ContainerRequestContext requestContext;
    private ContainerResponseContext responseContext;
    private UriInfo uriInfo;
    private MultivaluedMap<String, Object> responseHeaders;

    @BeforeEach
    void setUp() {
        filter = new CorrelationIdFilter();
        requestContext = mock(ContainerRequestContext.class);
        responseContext = mock(ContainerResponseContext.class);
        uriInfo = mock(UriInfo.class);
        responseHeaders = new MultivaluedHashMap<>();
        
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(responseContext.getHeaders()).thenReturn(responseHeaders);
    }

    @AfterEach
    void tearDown() {
        // Clean up MDC after each test
        MDC.clear();
    }

    @Test
    @DisplayName("Should generate new correlation ID when header is not present")
    void testFilter_WithoutCorrelationIdHeader_GeneratesNewId() {
        // Given
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(null);
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");

        // When
        filter.filter(requestContext);

        // Then
        Object correlationId = MDC.get("correlationId");
        assertNotNull(correlationId);
        assertFalse(correlationId.toString().isEmpty());
        // Verify MDC was set
        assertNotNull(MDC.get("correlationId"));
    }

    @Test
    @DisplayName("Should use correlation ID from request header when present")
    void testFilter_WithCorrelationIdHeader_UsesProvidedId() {
        // Given
        String providedCorrelationId = "test-correlation-id-123";
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(providedCorrelationId);
        when(requestContext.getMethod()).thenReturn("GET");
        when(uriInfo.getPath()).thenReturn("/api/v1/test");

        // When
        filter.filter(requestContext);

        // Then
        Object correlationId = MDC.get("correlationId");
        assertNotNull(correlationId);
        assertEquals(providedCorrelationId, correlationId.toString());
    }

    @Test
    @DisplayName("Should generate new correlation ID when header is blank")
    void testFilter_WithBlankCorrelationIdHeader_GeneratesNewId() {
        // Given
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn("   ");
        when(requestContext.getMethod()).thenReturn("PUT");
        when(uriInfo.getPath()).thenReturn("/api/v1/update");

        // When
        filter.filter(requestContext);

        // Then
        Object correlationId = MDC.get("correlationId");
        assertNotNull(correlationId);
        assertFalse(correlationId.toString().trim().isEmpty());
        // Should not be the blank string
        assertNotEquals("   ", correlationId.toString());
    }

    @Test
    @DisplayName("Should log incoming request with method and path")
    void testFilter_LogsIncomingRequest() {
        // Given
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(null);
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");

        // When
        filter.filter(requestContext);

        // Then
        // Verify that the method and path were accessed (indicating log was called)
        verify(requestContext, times(1)).getMethod();
        verify(uriInfo, times(1)).getPath();
    }

    @Test
    @DisplayName("Should add correlation ID to response header when present in MDC")
    void testFilterResponse_WithCorrelationIdInMDC_AddsToResponseHeader() {
        // Given
        String correlationId = "response-correlation-id-456";
        MDC.put("correlationId", correlationId);
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");
        when(responseContext.getStatus()).thenReturn(200);

        // When
        filter.filter(requestContext, responseContext);

        // Then
        assertTrue(responseHeaders.containsKey("X-Correlation-ID"));
        assertEquals(correlationId, responseHeaders.getFirst("X-Correlation-ID"));
    }

    @Test
    @DisplayName("Should not add correlation ID to response header when not present in MDC")
    void testFilterResponse_WithoutCorrelationIdInMDC_DoesNotAddHeader() {
        // Given
        // MDC is empty (no correlation ID set)
        when(requestContext.getMethod()).thenReturn("GET");
        when(uriInfo.getPath()).thenReturn("/api/v1/test");
        when(responseContext.getStatus()).thenReturn(404);

        // When
        filter.filter(requestContext, responseContext);

        // Then
        assertFalse(responseHeaders.containsKey("X-Correlation-ID"));
    }

    @Test
    @DisplayName("Should log outgoing response with status code")
    void testFilterResponse_LogsOutgoingResponse() {
        // Given
        String correlationId = "log-correlation-id-789";
        MDC.put("correlationId", correlationId);
        when(requestContext.getMethod()).thenReturn("DELETE");
        when(uriInfo.getPath()).thenReturn("/api/v1/delete");
        when(responseContext.getStatus()).thenReturn(204);

        // When
        filter.filter(requestContext, responseContext);

        // Then
        // Verify that status, method and path were accessed (indicating log was called)
        verify(responseContext, times(1)).getStatus();
        verify(requestContext, times(1)).getMethod();
        verify(uriInfo, times(1)).getPath();
    }

    @Test
    @DisplayName("Should clean up MDC after response processing")
    void testFilterResponse_CleansUpMDC() {
        // Given
        String correlationId = "cleanup-correlation-id-101";
        MDC.put("correlationId", correlationId);
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");
        when(responseContext.getStatus()).thenReturn(200);

        // When
        filter.filter(requestContext, responseContext);

        // Then
        // MDC should be cleaned up
        assertNull(MDC.get("correlationId"));
    }

    @Test
    @DisplayName("Should handle full request-response cycle with generated correlation ID")
    void testFullCycle_GeneratedCorrelationId() {
        // Given - Request
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(null);
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");
        when(responseContext.getStatus()).thenReturn(200);

        // When - Process request
        filter.filter(requestContext);
        Object generatedCorrelationId = MDC.get("correlationId");
        assertNotNull(generatedCorrelationId);

        // When - Process response
        filter.filter(requestContext, responseContext);

        // Then
        // Correlation ID should be in response header
        assertTrue(responseHeaders.containsKey("X-Correlation-ID"));
        assertEquals(generatedCorrelationId.toString(), responseHeaders.getFirst("X-Correlation-ID"));
        // MDC should be cleaned up
        assertNull(MDC.get("correlationId"));
    }

    @Test
    @DisplayName("Should handle full request-response cycle with provided correlation ID")
    void testFullCycle_ProvidedCorrelationId() {
        // Given - Request
        String providedCorrelationId = "provided-id-202";
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(providedCorrelationId);
        when(requestContext.getMethod()).thenReturn("GET");
        when(uriInfo.getPath()).thenReturn("/api/v1/test");
        when(responseContext.getStatus()).thenReturn(200);

        // When - Process request
        filter.filter(requestContext);
        assertEquals(providedCorrelationId, MDC.get("correlationId").toString());

        // When - Process response
        filter.filter(requestContext, responseContext);

        // Then
        // Correlation ID should be in response header
        assertTrue(responseHeaders.containsKey("X-Correlation-ID"));
        assertEquals(providedCorrelationId, responseHeaders.getFirst("X-Correlation-ID"));
        // MDC should be cleaned up
        assertNull(MDC.get("correlationId"));
    }

    @Test
    @DisplayName("Should handle different HTTP methods")
    void testFilter_WithDifferentHttpMethods() {
        // Given
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};
        
        for (String method : methods) {
            // Setup
            MDC.clear();
            when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(null);
            when(requestContext.getMethod()).thenReturn(method);
            when(uriInfo.getPath()).thenReturn("/api/v1/test");

            // When
            filter.filter(requestContext);

            // Then
            assertNotNull(MDC.get("correlationId"), "Correlation ID should be set for " + method);
            MDC.clear();
        }
    }

    @Test
    @DisplayName("Should handle different response status codes")
    void testFilterResponse_WithDifferentStatusCodes() {
        // Given
        int[] statusCodes = {200, 201, 400, 404, 500};
        String correlationId = "status-test-id-303";
        
        for (int statusCode : statusCodes) {
            // Setup
            MDC.clear();
            MDC.put("correlationId", correlationId);
            responseHeaders.clear();
            when(requestContext.getMethod()).thenReturn("POST");
            when(uriInfo.getPath()).thenReturn("/api/v1/test");
            when(responseContext.getStatus()).thenReturn(statusCode);

            // When
            filter.filter(requestContext, responseContext);

            // Then
            assertTrue(responseHeaders.containsKey("X-Correlation-ID"), 
                "Correlation ID should be in header for status " + statusCode);
            assertEquals(correlationId, responseHeaders.getFirst("X-Correlation-ID"));
            MDC.clear();
        }
    }

    @Test
    @DisplayName("Should generate valid UUID format for correlation ID")
    void testFilter_GeneratesValidUUID() {
        // Given
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(null);
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");

        // When
        filter.filter(requestContext);

        // Then
        Object correlationId = MDC.get("correlationId");
        assertNotNull(correlationId);
        String correlationIdStr = correlationId.toString();
        // UUID format: 8-4-4-4-12 hexadecimal characters
        assertTrue(correlationIdStr.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"),
            "Generated correlation ID should be a valid UUID format: " + correlationIdStr);
    }

    @Test
    @DisplayName("Should handle empty string correlation ID header")
    void testFilter_WithEmptyStringHeader_GeneratesNewId() {
        // Given
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn("");
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");

        // When
        filter.filter(requestContext);

        // Then
        Object correlationId = MDC.get("correlationId");
        assertNotNull(correlationId);
        assertFalse(correlationId.toString().isEmpty());
        // Should not be empty string
        assertNotEquals("", correlationId.toString());
    }

    @Test
    @DisplayName("Should handle response filter when correlation ID is null in MDC")
    void testFilterResponse_WithNullCorrelationIdInMDC_DoesNotAddHeader() {
        // Given
        // MDC is empty or correlationId is null
        MDC.remove("correlationId");
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");
        when(responseContext.getStatus()).thenReturn(500);

        // When
        filter.filter(requestContext, responseContext);

        // Then
        // Should not add header when correlation ID is null
        assertFalse(responseHeaders.containsKey("X-Correlation-ID"));
        // MDC should still be cleaned up
        assertNull(MDC.get("correlationId"));
    }

    @Test
    @DisplayName("Should handle extractOrGenerateCorrelationId with valid header")
    void testExtractOrGenerateCorrelationId_WithValidHeader() {
        // Given
        String validCorrelationId = "valid-correlation-id-999";
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(validCorrelationId);
        when(requestContext.getMethod()).thenReturn("GET");
        when(uriInfo.getPath()).thenReturn("/api/v1/test");

        // When
        filter.filter(requestContext);

        // Then
        Object correlationId = MDC.get("correlationId");
        assertNotNull(correlationId);
        assertEquals(validCorrelationId, correlationId.toString());
    }

    @Test
    @DisplayName("Should handle extractOrGenerateCorrelationId with null header")
    void testExtractOrGenerateCorrelationId_WithNullHeader() {
        // Given
        when(requestContext.getHeaderString("X-Correlation-ID")).thenReturn(null);
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");

        // When
        filter.filter(requestContext);

        // Then
        Object correlationId = MDC.get("correlationId");
        assertNotNull(correlationId);
        // Should be a valid UUID format
        String correlationIdStr = correlationId.toString();
        assertTrue(correlationIdStr.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
    }

    @Test
    @DisplayName("Should handle response filter when correlation ID object is not String")
    void testFilterResponse_WithNonStringCorrelationId() {
        // Given
        // Put a non-String object in MDC to test toString() conversion
        MDC.put("correlationId", 12345);
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("/api/v1/resend");
        when(responseContext.getStatus()).thenReturn(200);

        // When
        filter.filter(requestContext, responseContext);

        // Then
        // Should convert to string and add to header
        assertTrue(responseHeaders.containsKey("X-Correlation-ID"));
        assertEquals("12345", responseHeaders.getFirst("X-Correlation-ID"));
        // MDC should be cleaned up
        assertNull(MDC.get("correlationId"));
    }
}

