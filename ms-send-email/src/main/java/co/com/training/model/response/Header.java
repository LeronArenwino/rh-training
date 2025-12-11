package co.com.training.model.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Header", description = "Represents the header of the response with code and message")
public record Header(
        /*
         * Response code indicating the status of the operation
         * 
         * @param codeResponse The HTTP-like response code
         */
        @Schema(description = "Response code indicating the status of the operation", example = "200", required = true)
        Integer codeResponse,
        /*
         * Response message describing the operation result
         * 
         * @param messageResponse The message describing the response
         */
        @Schema(description = "Response message describing the operation result", example = "Sent", required = true)
        String messageResponse) {
}

