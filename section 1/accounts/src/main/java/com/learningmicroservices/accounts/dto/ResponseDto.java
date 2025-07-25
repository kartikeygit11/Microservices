package com.learningmicroservices.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
        name = "Response",
        description = "Schema to hold successful response information"
)
@Data
      // ← for Jackson deserialization
@AllArgsConstructor         // ← for convenient creation in controllers
public class ResponseDto {

    @Schema(description = "Status code in the response")
    private String statusCode;

    @Schema(description = "Status message in the response")
    private String statusMsg;
}
