package co.com.training.service;

import co.com.training.model.request.EmailRequest;
import co.com.training.model.response.DataResponse;
import io.smallrye.mutiny.Uni;

public interface EmailService {
    
    Uni<DataResponse> sendEmail(EmailRequest emailRequest);

}
