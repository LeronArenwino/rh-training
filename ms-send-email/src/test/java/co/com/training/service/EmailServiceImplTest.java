package co.com.training.service;

import co.com.training.model.request.EmailAttached;
import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import com.training.services.Attachment;
import com.training.services.ResendEmailRequest;
import com.training.services.ResendEmailResult;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailServiceImpl.
 * 
 * <p>Tests verify that EmailServiceImpl properly handles email resend operations,
 * including successful responses, business errors, technical errors, and attachment
 * handling.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@QuarkusTest
@DisplayName("EmailServiceImpl Tests")
class EmailServiceImplTest {

    @Inject
    EmailService emailService;

    @Inject
    TestMailServiceSoapProducer testMailServiceSoapProducer;

    private TestMailServiceSoapProducer.TestMailServiceSoap testMailServiceSoap;

    @BeforeEach
    void setUp() {
        testMailServiceSoap = testMailServiceSoapProducer.getTestMailServiceSoap();
        assertNotNull(testMailServiceSoap, "TestMailServiceSoap should be initialized");
        testMailServiceSoap.reset();
    }

    @Test
    @DisplayName("Should send email successfully without attachments")
    void testSendEmail_Success_WithoutAttachments() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        ResendEmailResult successResult = new ResendEmailResult();
        successResult.setSuccess(true);
        successResult.setMessage("Email sent successfully");
        testMailServiceSoap.setResult(successResult);

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertNotNull(response.header());
        assertEquals(200, response.header().codeResponse());
        assertEquals("Sent", response.header().messageResponse());
        assertNotNull(response.body());
        assertEquals("1", response.body().emailId());
        assertEquals("test@example.com", response.body().recipient());
        assertEquals("REENVIADO", response.body().status());
        assertEquals("Email sent successfully", response.body().detail());
    }

    @Test
    @DisplayName("Should send email successfully with attachments")
    void testSendEmail_Success_WithAttachments() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            List.of(
                new EmailAttached("1", "file1.pdf", "/path/to/file1.pdf"),
                new EmailAttached("2", "file2.jpg", "/path/to/file2.jpg")
            )
        );

        ResendEmailResult successResult = new ResendEmailResult();
        successResult.setSuccess(true);
        successResult.setMessage("Email with attachments sent successfully");
        testMailServiceSoap.setResult(successResult);

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(200, response.header().codeResponse());
        assertEquals("Email with attachments sent successfully", response.body().detail());
        
        // Verify attachments were converted
        ResendEmailRequest capturedRequest = testMailServiceSoap.getLastRequest();
        assertNotNull(capturedRequest);
        assertNotNull(capturedRequest.getAttachments());
        assertEquals(2, capturedRequest.getAttachments().getAttachment().size());
        
        Attachment attachment1 = capturedRequest.getAttachments().getAttachment().get(0);
        assertEquals("file1.pdf", attachment1.getName());
        assertEquals("/path/to/file1.pdf", attachment1.getContent());
        assertEquals("application/octet-stream", attachment1.getContentType());
    }

    @Test
    @DisplayName("Should handle success response with null message")
    void testSendEmail_Success_WithNullMessage() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        ResendEmailResult successResult = new ResendEmailResult();
        successResult.setSuccess(true);
        successResult.setMessage(null);
        testMailServiceSoap.setResult(successResult);

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(200, response.header().codeResponse());
        assertEquals("Email resent successfully to the SOAP service", response.body().detail());
    }

    @Test
    @DisplayName("Should handle business error with code 400")
    void testSendEmail_BusinessError_Code400() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        ResendEmailResult errorResult = new ResendEmailResult();
        errorResult.setSuccess(false);
        errorResult.setMessage("Invalid email address");
        errorResult.setErrorCode("400");
        testMailServiceSoap.setResult(errorResult);

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(400, response.header().codeResponse());
        assertEquals("Error", response.header().messageResponse());
        assertEquals("ERROR", response.body().status());
        assertEquals("Invalid email address", response.body().detail());
    }

    @Test
    @DisplayName("Should handle business error with code 409")
    void testSendEmail_BusinessError_Code409() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        ResendEmailResult errorResult = new ResendEmailResult();
        errorResult.setSuccess(false);
        errorResult.setMessage("Email already sent");
        errorResult.setErrorCode("409");
        testMailServiceSoap.setResult(errorResult);

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(409, response.header().codeResponse());
        assertEquals("Error", response.header().messageResponse());
        assertEquals("ERROR", response.body().status());
        assertEquals("Email already sent", response.body().detail());
    }

    @Test
    @DisplayName("Should convert invalid business error code to 400")
    void testSendEmail_BusinessError_InvalidCode() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        ResendEmailResult errorResult = new ResendEmailResult();
        errorResult.setSuccess(false);
        errorResult.setMessage("Some error");
        errorResult.setErrorCode("500"); // Invalid business error code
        testMailServiceSoap.setResult(errorResult);

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(400, response.header().codeResponse()); // Should be converted to 400
        assertEquals("Error", response.header().messageResponse());
    }

    @Test
    @DisplayName("Should handle business error with null error code")
    void testSendEmail_BusinessError_NullErrorCode() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        ResendEmailResult errorResult = new ResendEmailResult();
        errorResult.setSuccess(false);
        errorResult.setMessage("Business error");
        errorResult.setErrorCode(null);
        testMailServiceSoap.setResult(errorResult);

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(400, response.header().codeResponse()); // Should default to 400
        assertEquals("Business error", response.body().detail());
    }

    @Test
    @DisplayName("Should handle business error with null message")
    void testSendEmail_BusinessError_NullMessage() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        ResendEmailResult errorResult = new ResendEmailResult();
        errorResult.setSuccess(false);
        errorResult.setMessage(null);
        errorResult.setErrorCode("400");
        testMailServiceSoap.setResult(errorResult);

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(400, response.header().codeResponse());
        assertEquals("Business error received from the SOAP service", response.body().detail());
    }

    @Test
    @DisplayName("Should handle technical error with exception message")
    void testSendEmail_TechnicalError_WithMessage() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        testMailServiceSoap.setException(new RuntimeException("Connection timeout"));

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(500, response.header().codeResponse());
        assertEquals("Internal Server Error", response.header().messageResponse());
        assertEquals("ERROR", response.body().status());
        assertTrue(response.body().detail().contains("Technical error consuming the WSDL"));
        assertTrue(response.body().detail().contains("Connection timeout"));
    }

    @Test
    @DisplayName("Should handle technical error without exception message")
    void testSendEmail_TechnicalError_WithoutMessage() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        testMailServiceSoap.setException(new RuntimeException());

        // When
        Uni<DataResponse> responseUni = emailService.sendEmail(emailRequest);
        DataResponse response = responseUni.await().indefinitely();

        // Then
        assertNotNull(response);
        assertEquals(500, response.header().codeResponse());
        assertTrue(response.body().detail().contains("Technical error consuming the WSDL"));
        assertTrue(response.body().detail().contains("timeout, connection failure, 500 from provider"));
    }

    @Test
    @DisplayName("Should build SOAP request correctly without attachments")
    void testBuildSoapRequest_WithoutAttachments() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            null
        );

        ResendEmailResult successResult = new ResendEmailResult();
        successResult.setSuccess(true);
        successResult.setMessage("Success");
        testMailServiceSoap.setResult(successResult);

        // When
        emailService.sendEmail(emailRequest).await().indefinitely();

        // Then
        ResendEmailRequest capturedRequest = testMailServiceSoap.getLastRequest();
        assertNotNull(capturedRequest);
        assertEquals("1", capturedRequest.getEmailId());
        assertEquals("test@example.com", capturedRequest.getRecipient());
        assertEquals("Test Subject", capturedRequest.getSubject());
        assertEquals("Test Message", capturedRequest.getMessage());
        assertNull(capturedRequest.getAttachments());
    }

    @Test
    @DisplayName("Should build SOAP request correctly with empty attachments list")
    void testBuildSoapRequest_WithEmptyAttachments() {
        // Given
        EmailRequest emailRequest = new EmailRequest(
            "1",
            "test@example.com",
            "Test Subject",
            "Test Message",
            List.of()
        );

        ResendEmailResult successResult = new ResendEmailResult();
        successResult.setSuccess(true);
        successResult.setMessage("Success");
        testMailServiceSoap.setResult(successResult);

        // When
        emailService.sendEmail(emailRequest).await().indefinitely();

        // Then
        ResendEmailRequest capturedRequest = testMailServiceSoap.getLastRequest();
        assertNotNull(capturedRequest);
        assertNull(capturedRequest.getAttachments()); // Empty list should not set attachments
    }

}

