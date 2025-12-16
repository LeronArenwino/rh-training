package co.com.training.model.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Represents the complete response data structure for email operations.
 * 
 * <p>This record encapsulates both the header and body of the response,
 * providing a standardized format for all API responses. The header contains
 * the response code and message, while the body contains the specific details
 * of the operation result.</p>
 * 
 * <p>This structure is used for both successful and error responses,
 * ensuring consistency across all API endpoints.</p>
 * 
 * @param header The response header containing code and message
 * @param body The response body containing email operation details
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
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
