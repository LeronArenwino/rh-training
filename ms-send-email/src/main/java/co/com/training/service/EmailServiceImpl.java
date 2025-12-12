package co.com.training.service;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import co.com.training.model.response.Header;
import co.com.training.model.response.Body;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailServiceImpl implements EmailService{

    @Override
    public Uni<DataResponse> sendEmail(EmailRequest emailRequest) {
        // Temporary response
        Header header = new Header(200, "Sent");
        Body body = new Body(
            emailRequest.emailId(),
            emailRequest.recipient(),
            "SUCCESS",
            "Email sent successfully to the SOAP service"
        );
        DataResponse response = new DataResponse(header, body);
        return Uni.createFrom().item(response);
    }

}
