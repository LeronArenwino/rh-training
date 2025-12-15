package co.com.training.service;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import co.com.training.model.response.Body;
import com.training.services.MailServiceSoap;
import com.training.services.ResendEmailRequest;
import com.training.services.ResendEmailResult;
import com.training.services.ArrayOfAttachment;
import com.training.services.Attachment;
import io.quarkiverse.cxf.annotation.CXFClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailServiceImpl implements EmailService{

    private static final Logger LOG = Logger.getLogger(EmailServiceImpl.class);

    @Inject
    @CXFClient("mailService")
    MailServiceSoap mailServiceSoap;

    @Override
    public Uni<DataResponse> sendEmail(EmailRequest emailRequest) {
        return Uni.createFrom().item(() -> {
            try {
                // Convert EmailRequest to ResendEmailRequest of SOAP
                ResendEmailRequest soapRequest = new ResendEmailRequest();
                soapRequest.setEmailId(emailRequest.emailId());
                soapRequest.setRecipient(emailRequest.recipient());
                soapRequest.setSubject(emailRequest.subject());
                soapRequest.setMessage(emailRequest.message());
                
                // Convert attachments if they exist
                if (emailRequest.emailAttacheds() != null && !emailRequest.emailAttacheds().isEmpty()) {
                    ArrayOfAttachment attachments = new ArrayOfAttachment();
                    emailRequest.emailAttacheds().forEach(attached -> {
                        Attachment attachment = new Attachment();
                        attachment.setName(attached.attachedName());
                        attachment.setContent(attached.attachedPath()); // Adjust according to your logic
                        attachment.setContentType("application/octet-stream");
                        attachments.getAttachment().add(attachment);
                    });
                    soapRequest.setAttachments(attachments);
                }

                // Call the SOAP service
                ResendEmailResult soapResult = mailServiceSoap.resendEmail(soapRequest);

                // Convert SOAP response to DataResponse
                return mapSoapResponseToDataResponse(emailRequest, soapResult);
                
            } catch (Exception e) {
                LOG.error("Error calling SOAP service", e);
                // Handle technical errors (500)
                return createErrorResponse(emailRequest, 500, "Internal Server Error", 
                    "Technical failure: " + e.getMessage());
            }
        });
    }

    private DataResponse mapSoapResponseToDataResponse(EmailRequest emailRequest, ResendEmailResult soapResult) {
        if (soapResult.isSuccess()) {
            // Successful response
            Header header = new Header(200, "Sent");
            Body body = new Body(
                emailRequest.emailId(),
                emailRequest.recipient(),
                "REENVIADO",
                soapResult.getMessage() != null ? soapResult.getMessage() : "Email sent successfully to the SOAP service"
            );
            return new DataResponse(header, body);
        } else {
            // Business error
            int statusCode = soapResult.getErrorCode() != null ? 
                Integer.parseInt(soapResult.getErrorCode()) : 400;
            Header header = new Header(statusCode, "Error");
            Body body = new Body(
                emailRequest.emailId(),
                emailRequest.recipient(),
                "ERROR",
                soapResult.getMessage() != null ? soapResult.getMessage() : "Business error occurred"
            );
            return new DataResponse(header, body);
        }
    }

    private DataResponse createErrorResponse(EmailRequest emailRequest, int code, String message, String detail) {
        Header header = new Header(code, message);
        Body body = new Body(
            emailRequest.emailId(),
            emailRequest.recipient(),
            "ERROR",
            detail
        );
        return new DataResponse(header, body);
    }
}
