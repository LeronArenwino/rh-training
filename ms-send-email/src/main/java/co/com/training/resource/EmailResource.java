package co.com.training.resource;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import co.com.training.service.EmailService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmailResource {
    
    private static final Logger LOG = Logger.getLogger(EmailResource.class);
    private final EmailService emailService;

    @Inject
    public EmailResource(EmailService emailService) {
        this.emailService = emailService;
    }

    @POST
    @Path("/resend")
    public Uni<DataResponse> resendEmail(@RequestBody @Valid EmailRequest emailRequest) {
        LOG.info("Resending email to: " + emailRequest.recipient());
        return emailService.sendEmail(emailRequest);
    }
}
