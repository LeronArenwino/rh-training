package co.com.training.model.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "DataResponse", description = "Represents the complete response data of the operation, including header and body")
public record DataResponse(
        /*
         * Response header containing code and message
         * 
         * @param header The header with response code and message
         */
        @Schema(description = "Response header containing code and message", required = true)
        Header header,
        /*
         * Response body containing email details
         * 
         * @param body The body with email operation details
         */
        @Schema(description = "Response body containing email details", required = true)
        Body body) {
}
