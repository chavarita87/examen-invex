package com.invex.examen.dto;

import static com.invex.examen.constant.EmployeeConstants.MAX_AGE;
import static com.invex.examen.constant.EmployeeConstants.MIN_AGE;
import static com.invex.examen.constant.EmployeeConstants.NAME_MAX_LENGTH;
import static com.invex.examen.constant.EmployeeConstants.POSITION_MAX_LENGTH;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.invex.examen.constant.EmployeeConstants;
import com.invex.examen.constant.ValidationMessages;
import com.invex.examen.model.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;

/**
 * Fields that can be updated on an employee. Every field is optional:
 * only the ones sent in the request are changed, but the ones sent must be valid.
 */
@Builder
@Schema(description = "Fields to update; only the fields sent are changed")
public record EmployeeUpdateRequest(

        @Schema(description = "First name", example = "Juan")
        @Size(max = NAME_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NAME_PATTERN, message = ValidationMessages.INVALID_NAME)
        String firstName,

        @Schema(description = "Middle name", example = "Carlos")
        @Size(max = NAME_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NAME_PATTERN, message = ValidationMessages.INVALID_NAME)
        String middleName,

        @Schema(description = "Paternal last name", example = "Perez")
        @Size(max = NAME_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NAME_PATTERN, message = ValidationMessages.INVALID_NAME)
        String paternalLastName,

        @Schema(description = "Maternal last name", example = "Lopez")
        @Size(max = NAME_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NAME_PATTERN, message = ValidationMessages.INVALID_NAME)
        String maternalLastName,

        @Schema(description = "Age in years; must match the birth date", example = "31")
        @Min(value = MIN_AGE, message = ValidationMessages.MIN_AGE)
        @Max(value = MAX_AGE, message = ValidationMessages.MAX_AGE)
        Integer age,

        @Schema(description = "Gender", example = "MALE")
        Gender gender,

        @Schema(description = "Birth date (dd-MM-yyyy)", type = "string", pattern = "\\d{2}-\\d{2}-\\d{4}",
                example = "15-03-1995")
        @Past(message = ValidationMessages.PAST_DATE)
        @JsonFormat(pattern = EmployeeConstants.DATE_FORMAT)
        LocalDate birthDate,

        @Schema(description = "Job position", example = "Senior Developer")
        @Size(max = POSITION_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NOT_BLANK_PATTERN, message = ValidationMessages.NOT_BLANK)
        String position,

        @Schema(description = "Whether the employee is active", example = "false")
        Boolean active) {
}
