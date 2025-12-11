package co.com.training.model.request;

import java.util.List;

public record EmailRequest(
    String emailId,
    String emailTo,
    String emailSubject,
    String emailBody,
    List<EmailAttached> emailAttacheds
) {
}