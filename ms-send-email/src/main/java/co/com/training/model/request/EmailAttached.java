package co.com.training.model.request;

public record EmailAttached(
    String attachedId,
    String attachedName,
    String attachedPath
) {
}
