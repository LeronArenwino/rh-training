package co.com.training.model.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Represents the header section of an API response.
 * 
 * <p>The header contains metadata about the response, including the HTTP-like
 * response code and a descriptive message indicating the result of the operation.</p>
 * 
 * <p>Common response codes:
 * <ul>
 *   <li>200 - Success</li>
 *   <li>400 - Bad Request (validation errors)</li>
 *   <li>404 - Not Found (business errors)</li>
 *   <li>500 - Internal Server Error (technical failures)</li>
 * </ul>
 * </p>
 * 
 * @param codeResponse The HTTP-like response code indicating the status
 * @param messageResponse The descriptive message about the operation result
 * 
 * @author Francisco Dueñas
 * @since 1.0.0
 */
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

