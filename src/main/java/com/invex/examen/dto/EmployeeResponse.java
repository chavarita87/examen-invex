package com.invex.examen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.invex.examen.constant.EmployeeConstants;
import com.invex.examen.model.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

/**
 * Employee as returned by the API.
 */
@Builder
@Schema(description = "Employee as returned by the API")
public record EmployeeResponse(
        @Schema(description = "Employee id", example = "1") Long id,
        @Schema(description = "First name", example = "Juan") String firstName,
        @Schema(description = "Middle name", example = "Carlos") String middleName,
        @Schema(description = "Paternal last name", example = "Perez") String paternalLastName,
        @Schema(description = "Maternal last name", example = "Lopez") String maternalLastName,
        @Schema(description = "Age in years", example = "31") Integer age,
        @Schema(description = "Gender", example = "MALE") Gender gender,
        @Schema(description = "Birth date (dd-MM-yyyy)", type = "string", example = "15-03-1995")
        @JsonFormat(pattern = EmployeeConstants.DATE_FORMAT) LocalDate birthDate,
        @Schema(description = "Job position", example = "Developer") String position,
        @Schema(description = "Date and time the employee was registered (dd-MM-yyyy HH:mm:ss)",
                type = "string", example = "26-11-2026 18:38:04")
        @JsonFormat(pattern = EmployeeConstants.DATE_TIME_FORMAT) LocalDateTime createdAt,
        @Schema(description = "Whether the employee is active", example = "true") Boolean active) {
}
