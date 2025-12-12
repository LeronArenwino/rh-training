package co.com.training.service;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailServiceImpl implements EmailService{

    @Override
    public Uni<DataResponse> sendEmail(EmailRequest emailRequest) {
        return Uni.createFrom().item(null);
    }

}
