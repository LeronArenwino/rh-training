package co.com.training.model.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "DataResponse", description = "Represents the response data of the operation, including the success status, message, and technical failure message")
public record DataResponse(
        /*
         * Indicates whether the operation was successful or not
         * 
         * @param success {@code true} if the operation was successful,
         * {@code false} otherwise
         */
        @Schema(description = "Indicates whether the operation was successful or not", example = "true", required = true) 
        Boolean success,
        /*
         * Description status of the operation
         * This message provides detailed information about the operation result
         * @param message The message of the response
         */
        @Schema(description = "Description status of the operation", example = "Operation successful", required = true)
        String message,
        /*
         * Technical failure message
         * This message provides detailed information about the technical failure
         * @param technicalFailure The technical failure message
         */
        @Schema(description = "Technical failure message", example = "Technical failure", required = true)
        String technicalFailure) {
}
