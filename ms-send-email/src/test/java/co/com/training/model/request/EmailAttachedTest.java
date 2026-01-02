package co.com.training.model.request;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailAttached.
 * 
 * <p>Tests verify that EmailAttached record can be created with various
 * combinations of field values, including null values, and that all
 * accessor methods work correctly.</p>
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
@QuarkusTest
@DisplayName("EmailAttached Tests")
class EmailAttachedTest {

    @ParameterizedTest
    @DisplayName("Should create EmailAttached with various field combinations")
    @MethodSource("emailAttachedProvider")
    void testCreateEmailAttached(String attachedId, String attachedName, String attachedPath) {
        // When
        EmailAttached emailAttached = new EmailAttached(attachedId, attachedName, attachedPath);

        // Then
        assertNotNull(emailAttached);
        if (attachedId == null) {
            assertNull(emailAttached.attachedId());
        } else {
            assertEquals(attachedId, emailAttached.attachedId());
        }
        if (attachedName == null) {
            assertNull(emailAttached.attachedName());
        } else {
            assertEquals(attachedName, emailAttached.attachedName());
        }
        if (attachedPath == null) {
            assertNull(emailAttached.attachedPath());
        } else {
            assertEquals(attachedPath, emailAttached.attachedPath());
        }
    }

    static Stream<Arguments> emailAttachedProvider() {
        return Stream.of(
            // All fields
            Arguments.of("1", "document.pdf", "/path/to/document.pdf"),
            // Null values
            Arguments.of(null, null, null),
            // Empty strings
            Arguments.of("", "", ""),
            // Mixed null and non-null values
            Arguments.of("1", null, "/path/to/file.pdf"),
            // Long strings
            Arguments.of("12345678901234567890", 
                "very-long-document-name-with-many-characters.pdf",
                "/very/long/path/to/the/document/file/very-long-document-name-with-many-characters.pdf"),
            // Different file types
            Arguments.of("1", "document.pdf", "/path/to/document.pdf"),
            Arguments.of("2", "image.jpg", "/path/to/image.jpg"),
            Arguments.of("3", "text.txt", "/path/to/text.txt"),
            // Special characters
            Arguments.of("id-123", "file-name_with.special-chars.pdf", "/path/to/file-name_with.special-chars.pdf")
        );
    }

    @Test
    @DisplayName("Should test equals and hashCode for EmailAttached")
    void testEqualsAndHashCode() {
        // Given
        EmailAttached emailAttached1 = new EmailAttached("1", "test.pdf", "/path/to/test.pdf");
        EmailAttached emailAttached2 = new EmailAttached("1", "test.pdf", "/path/to/test.pdf");

        // Then
        assertEquals(emailAttached1, emailAttached2);
        assertEquals(emailAttached1.hashCode(), emailAttached2.hashCode());
    }

    @Test
    @DisplayName("Should test equals returns false for different values")
    void testEqualsReturnsFalseForDifferentValues() {
        // Given
        EmailAttached emailAttached1 = new EmailAttached("1", "test.pdf", "/path/to/test.pdf");
        EmailAttached emailAttached2 = new EmailAttached("2", "test.pdf", "/path/to/test.pdf");

        // Then
        assertNotEquals(emailAttached1, emailAttached2);
    }

    @Test
    @DisplayName("Should test toString for EmailAttached")
    void testToString() {
        // Given
        EmailAttached emailAttached = new EmailAttached("1", "test.pdf", "/path/to/test.pdf");

        // When
        String toString = emailAttached.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("EmailAttached"));
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("test.pdf"));
        assertTrue(toString.contains("/path/to/test.pdf"));
    }

}