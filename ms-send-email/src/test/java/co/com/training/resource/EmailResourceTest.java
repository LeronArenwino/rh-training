package co.com.training.resource;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.Body;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import co.com.training.service.EmailService;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailResource.
 * 
 * <p>Tests verify that the REST endpoint properly handles email resend requests,
 * delegates to the email service, and correctly maps response codes to HTTP status codes.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@QuarkusTest
@DisplayName("EmailResource Tests")
class EmailResourceTest {

    private EmailResource emailResource;
    private TestEmailService testEmailService;

    @BeforeEach
    void setUp() {
        testEmailService = new TestEmailService();
        emailResource = new EmailResource(testEmailService);
    }

    @Test
    @DisplayName("Should create EmailResource with injected service")
    void testConstructor() {
        // Given
        EmailService service = new TestEmailService();

        // When
        EmailResource resource = new EmailResource(service);

        // Then
        assertNotNull(resource);
    }

    @Test
    @DisplayName("Should return 200 status code for successful email resend")
    void testResendEmail_WithSuccess_ReturnsOk() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );
        
        Header header = new Header(200, "Sent");
        Body body = new Body("1", "test@example.com", "RESENT", "Email resent successfully");
        DataResponse dataResponse = new DataResponse(header, body);
        
        testEmailService.setResponse(dataResponse);

        // When
        Uni<Response> responseUni = emailResource.resendEmail(emailRequest);
        Response response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof DataResponse);
    }

    @Test
    @DisplayName("Should return 400 status code for business error")
    void testResendEmail_WithBusinessError400_ReturnsBadRequest() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );
        
        Header header = new Header(400, "Business Error");
        Body body = new Body("1", "test@example.com", "ERROR", "Invalid email ID");
        DataResponse dataResponse = new DataResponse(header, body);
        
        testEmailService.setResponse(dataResponse);

        // When
        Uni<Response> responseUni = emailResource.resendEmail(emailRequest);
        Response response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        DataResponse responseEntity = (DataResponse) response.getEntity();
        assertEquals(400, responseEntity.header().codeResponse());
    }

    @Test
    @DisplayName("Should return 409 status code for conflict error")
    void testResendEmail_WithConflictError409_ReturnsConflict() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );
        
        Header header = new Header(409, "Conflict");
        Body body = new Body("1", "test@example.com", "ERROR", "Email already sent");
        DataResponse dataResponse = new DataResponse(header, body);
        
        testEmailService.setResponse(dataResponse);

        // When
        Uni<Response> responseUni = emailResource.resendEmail(emailRequest);
        Response response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        DataResponse responseEntity = (DataResponse) response.getEntity();
        assertEquals(409, responseEntity.header().codeResponse());
    }

    @Test
    @DisplayName("Should return 500 status code for technical error")
    void testResendEmail_WithTechnicalError500_ReturnsInternalServerError() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );
        
        Header header = new Header(500, "Technical Error");
        Body body = new Body("1", "test@example.com", "ERROR", "Service unavailable");
        DataResponse dataResponse = new DataResponse(header, body);
        
        testEmailService.setResponse(dataResponse);

        // When
        Uni<Response> responseUni = emailResource.resendEmail(emailRequest);
        Response response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        DataResponse responseEntity = (DataResponse) response.getEntity();
        assertEquals(500, responseEntity.header().codeResponse());
    }

    @Test
    @DisplayName("Should return 500 status code for unknown response code")
    void testResendEmail_WithUnknownCode_ReturnsInternalServerError() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );
        
        Header header = new Header(503, "Service Unavailable");
        Body body = new Body("1", "test@example.com", "ERROR", "Unknown error");
        DataResponse dataResponse = new DataResponse(header, body);
        
        testEmailService.setResponse(dataResponse);

        // When
        Uni<Response> responseUni = emailResource.resendEmail(emailRequest);
        Response response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    @DisplayName("Should return 500 status code when codeResponse is null")
    void testResendEmail_WithNullCodeResponse_ReturnsInternalServerError() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );
        
        Header header = new Header(null, "Unknown");
        Body body = new Body("1", "test@example.com", "ERROR", "Error with null code");
        DataResponse dataResponse = new DataResponse(header, body);
        
        testEmailService.setResponse(dataResponse);

        // When
        Uni<Response> responseUni = emailResource.resendEmail(emailRequest);
        Response response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when emailRequest is null")
    void testResendEmail_WithNullRequest_ThrowsIllegalArgumentException() {
        // Given
        EmailRequest emailRequest = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailResource.resendEmail(emailRequest);
        });
    }

    @Test
    @DisplayName("Should return correct response entity structure")
    void testResendEmail_ReturnsCorrectResponseEntity() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );
        
        Header header = new Header(200, "Sent");
        Body body = new Body("1", "test@example.com", "RESENT", "Email resent successfully");
        DataResponse dataResponse = new DataResponse(header, body);
        
        testEmailService.setResponse(dataResponse);

        // When
        Uni<Response> responseUni = emailResource.resendEmail(emailRequest);
        Response response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof DataResponse);
        
        DataResponse responseEntity = (DataResponse) response.getEntity();
        assertNotNull(responseEntity.header());
        assertNotNull(responseEntity.body());
        assertEquals(200, responseEntity.header().codeResponse());
        assertEquals("Sent", responseEntity.header().messageResponse());
        assertEquals("1", responseEntity.body().emailId());
        assertEquals("test@example.com", responseEntity.body().recipient());
        assertEquals("RESENT", responseEntity.body().status());
    }

    @Test
    @DisplayName("Should handle email request with attachments")
        void testResendEmail_WithAttachments_ProcessesCorrectly() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            java.util.List.of(
                new co.com.training.model.request.EmailAttached("1", "file.pdf", "/path/to/file.pdf")
            )
        );
        
        Header header = new Header(200, "Sent");
        Body body = new Body("1", "test@example.com", "RESENT", "Email with attachments resent successfully");
        DataResponse dataResponse = new DataResponse(header, body);
        
        testEmailService.setResponse(dataResponse);

        // When
        Uni<Response> responseUni = emailResource.resendEmail(emailRequest);
        Response response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    /**
     * Test implementation of EmailService for testing purposes.
     * This allows us to control the response without using Mockito.
     */
    private static class TestEmailService implements EmailService {
        private DataResponse response;

        public void setResponse(DataResponse response) {
            this.response = response;
        }

        @Override
        public Uni<DataResponse> sendEmail(EmailRequest emailRequest) {
            return Uni.createFrom().item(response);
        }
    }
}
