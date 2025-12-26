package co.com.training.service;

import com.training.services.MailServiceSoap;
import com.training.services.ResendEmailRequest;
import com.training.services.ResendEmailResult;
import io.quarkiverse.cxf.annotation.CXFClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

/**
 * Producer for test MailServiceSoap implementation.
 */
@ApplicationScoped
public class TestMailServiceSoapProducer {

    private final TestMailServiceSoap testMailServiceSoap = new TestMailServiceSoap();

    @Produces
    @ApplicationScoped
    @Alternative
    @jakarta.annotation.Priority(1)
    @CXFClient("mailService")
    MailServiceSoap produceTestMailServiceSoap() {
        return testMailServiceSoap;
    }

    public TestMailServiceSoap getTestMailServiceSoap() {
        return testMailServiceSoap;
    }

    /**
     * Test implementation of MailServiceSoap for testing purposes.
     */
    static class TestMailServiceSoap implements MailServiceSoap {
        private ResendEmailResult result;
        private RuntimeException exception;
        private ResendEmailRequest lastRequest;

        public void setResult(ResendEmailResult result) {
            this.result = result;
            this.exception = null;
        }

        public void setException(RuntimeException exception) {
            this.exception = exception;
            this.result = null;
        }

        public ResendEmailRequest getLastRequest() {
            return lastRequest;
        }

        public void reset() {
            this.result = null;
            this.exception = null;
            this.lastRequest = null;
        }

        @Override
        public ResendEmailResult resendEmail(ResendEmailRequest request) {
            this.lastRequest = request;
            if (exception != null) {
                throw exception;
            }
            return result;
        }
    }
}

