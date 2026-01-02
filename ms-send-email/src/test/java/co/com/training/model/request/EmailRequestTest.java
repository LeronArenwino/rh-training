package co.com.training.model.request;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("EmailRequest Tests")
class EmailRequestTest {

    @Test
    @DisplayName("Should create EmailRequest with all fields")
    void testCreateEmailRequestWithAllFields() {
        // Given
        String emailId = "1";
        String recipient = "developer@training.com";
        String subject = "Test email";
        String message = "This is a test email";
        List<EmailAttached> emailAttacheds = List.of(
            new EmailAttached("1", "test.pdf", "/path/to/test.pdf")
        );

        // When
        EmailRequest emailRequest = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            emailAttacheds
        );

        // Then
        assertNotNull(emailRequest);
        assertEquals(emailId, emailRequest.emailId());
        assertEquals(recipient, emailRequest.recipient());
        assertEquals(subject, emailRequest.subject());
        assertEquals(message, emailRequest.message());
        assertNotNull(emailRequest.emailAttacheds());
        assertEquals(1, emailRequest.emailAttacheds().size());
        assertEquals("1", emailRequest.emailAttacheds().get(0).attachedId());
    }

    @Test
    @DisplayName("Should create EmailRequest with null emailAttacheds")
    void testCreateEmailRequestWithNullEmailAttacheds() {
        // Given
        String emailId = "2";
        String recipient = "user@example.com";
        String subject = "Another test";
        String message = "Another test message";

        // When
        EmailRequest emailRequest = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            null
        );

        // Then
        assertNotNull(emailRequest);
        assertEquals(emailId, emailRequest.emailId());
        assertEquals(recipient, emailRequest.recipient());
        assertEquals(subject, emailRequest.subject());
        assertEquals(message, emailRequest.message());
        assertNull(emailRequest.emailAttacheds());
    }

    @Test
    @DisplayName("Should create EmailRequest with empty emailAttacheds list")
    void testCreateEmailRequestWithEmptyEmailAttacheds() {
        // Given
        String emailId = "3";
        String recipient = "test@example.com";
        String subject = "Empty attachments test";
        String message = "Test with empty attachments";
        List<EmailAttached> emailAttacheds = new ArrayList<>();

        // When
        EmailRequest emailRequest = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            emailAttacheds
        );

        // Then
        assertNotNull(emailRequest);
        assertEquals(emailId, emailRequest.emailId());
        assertEquals(recipient, emailRequest.recipient());
        assertEquals(subject, emailRequest.subject());
        assertEquals(message, emailRequest.message());
        assertNotNull(emailRequest.emailAttacheds());
        assertTrue(emailRequest.emailAttacheds().isEmpty());
    }

    @Test
    @DisplayName("Should create EmailRequest with multiple emailAttacheds")
    void testCreateEmailRequestWithMultipleEmailAttacheds() {
        // Given
        String emailId = "4";
        String recipient = "multi@example.com";
        String subject = "Multiple attachments test";
        String message = "Test with multiple attachments";
        List<EmailAttached> emailAttacheds = List.of(
            new EmailAttached("1", "file1.pdf", "/path/to/file1.pdf"),
            new EmailAttached("2", "file2.jpg", "/path/to/file2.jpg"),
            new EmailAttached("3", "file3.txt", "/path/to/file3.txt")
        );

        // When
        EmailRequest emailRequest = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            emailAttacheds
        );

        // Then
        assertNotNull(emailRequest);
        assertEquals(emailId, emailRequest.emailId());
        assertEquals(recipient, emailRequest.recipient());
        assertEquals(subject, emailRequest.subject());
        assertEquals(message, emailRequest.message());
        assertNotNull(emailRequest.emailAttacheds());
        assertEquals(3, emailRequest.emailAttacheds().size());
        assertEquals("1", emailRequest.emailAttacheds().get(0).attachedId());
        assertEquals("2", emailRequest.emailAttacheds().get(1).attachedId());
        assertEquals("3", emailRequest.emailAttacheds().get(2).attachedId());
    }

    @Test
    @DisplayName("Should create EmailRequest with different email formats")
    void testCreateEmailRequestWithDifferentEmailFormats() {
        // Given
        String emailId = "5";
        String recipient = "user+tag@example.co.uk";
        String subject = "Email format test";
        String message = "Test with different email format";

        // When
        EmailRequest emailRequest = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            null
        );

        // Then
        assertNotNull(emailRequest);
        assertEquals(emailId, emailRequest.emailId());
        assertEquals(recipient, emailRequest.recipient());
        assertEquals(subject, emailRequest.subject());
        assertEquals(message, emailRequest.message());
    }

    @Test
    @DisplayName("Should create EmailRequest with empty strings")
    void testCreateEmailRequestWithEmptyStrings() {
        // Given
        String emailId = "";
        String recipient = "";
        String subject = "";
        String message = "";

        // When
        EmailRequest emailRequest = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            null
        );

        // Then
        assertNotNull(emailRequest);
        assertEquals(emailId, emailRequest.emailId());
        assertEquals(recipient, emailRequest.recipient());
        assertEquals(subject, emailRequest.subject());
        assertEquals(message, emailRequest.message());
    }

    @Test
    @DisplayName("Should test equals and hashCode for EmailRequest")
    void testEqualsAndHashCode() {
        // Given
        String emailId = "6";
        String recipient = "test@example.com";
        String subject = "Equality test";
        String message = "Test equality";
        List<EmailAttached> emailAttacheds = List.of(
            new EmailAttached("1", "test.pdf", "/path/to/test.pdf")
        );

        EmailRequest emailRequest1 = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            emailAttacheds
        );

        EmailRequest emailRequest2 = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            emailAttacheds
        );

        // Then
        assertEquals(emailRequest1, emailRequest2);
        assertEquals(emailRequest1.hashCode(), emailRequest2.hashCode());
    }

    @Test
    @DisplayName("Should test toString for EmailRequest")
    void testToString() {
        // Given
        String emailId = "7";
        String recipient = "test@example.com";
        String subject = "ToString test";
        String message = "Test toString";
        EmailRequest emailRequest = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            null
        );

        // When
        String toString = emailRequest.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("EmailRequest"));
        assertTrue(toString.contains(emailId));
        assertTrue(toString.contains(recipient));
        assertTrue(toString.contains(subject));
        assertTrue(toString.contains(message));
    }

    @Test
    @DisplayName("Should create EmailRequest with long strings")
    void testCreateEmailRequestWithLongStrings() {
        // Given
        String emailId = "123456789";
        String recipient = "very.long.email.address@very.long.domain.name.example.com";
        String subject = "This is a very long subject line that contains many words and characters";
        String message = "This is a very long message that contains many words and characters. "
            + "It is used to test that the EmailRequest record can handle long strings properly. "
            + "The message can contain multiple sentences and paragraphs.";

        // When
        EmailRequest emailRequest = new EmailRequest(
            emailId,
            recipient,
            subject,
            message,
            null
        );

        // Then
        assertNotNull(emailRequest);
        assertEquals(emailId, emailRequest.emailId());
        assertEquals(recipient, emailRequest.recipient());
        assertEquals(subject, emailRequest.subject());
        assertEquals(message, emailRequest.message());
    }
}

