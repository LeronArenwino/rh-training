package co.com.training.model.request;

/**
 * Represents an email attachment in the request.
 * 
 * <p>This record contains the information about a file attachment that can be
 * included with the email being resent. All fields are optional and can be null.</p>
 * 
 * @param attachedId The unique identifier of the attachment
 * @param attachedName The name of the attachment file
 * @param attachedPath The path or content of the attachment
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
public record EmailAttached(
    String attachedId,
    String attachedName,
    String attachedPath
) {
}
