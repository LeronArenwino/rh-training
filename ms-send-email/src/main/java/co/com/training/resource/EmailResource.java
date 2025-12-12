package co.com.training.resource;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import co.com.training.service.EmailService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/v1")
public class EmailResource {
    
    private final EmailService emailService;

    @Inject
    public EmailResource(EmailService emailService) {
        this.emailService = emailService;
    }

    @POST
    @Path("/resend")
    public Uni<DataResponse> resendEmail(EmailRequest emailRequest) {
        return emailService.sendEmail(emailRequest);
    }
}
