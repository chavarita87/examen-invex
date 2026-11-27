package com.invex.examen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.invex.examen.constant.EmployeeConstants;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

/**
 * Error body returned by every failed request.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        @JsonFormat(pattern = EmployeeConstants.DATE_TIME_FORMAT) LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDetail> fieldErrors) {

    /**
     * A single invalid field and the reason it was rejected.
     */
    public record FieldErrorDetail(String field, String message) {
    }
}
