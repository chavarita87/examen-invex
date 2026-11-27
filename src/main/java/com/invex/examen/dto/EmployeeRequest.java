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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;

/**
 * Data required to create an employee.
 */
@Builder
@Schema(description = "Data required to create an employee")
public record EmployeeRequest(

        @Schema(description = "First name", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = ValidationMessages.REQUIRED)
        @Size(max = NAME_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NAME_PATTERN, message = ValidationMessages.INVALID_NAME)
        String firstName,

        @Schema(description = "Middle name", example = "Carlos")
        @Size(max = NAME_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NAME_PATTERN, message = ValidationMessages.INVALID_NAME)
        String middleName,

        @Schema(description = "Paternal last name", example = "Perez", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = ValidationMessages.REQUIRED)
        @Size(max = NAME_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NAME_PATTERN, message = ValidationMessages.INVALID_NAME)
        String paternalLastName,

        @Schema(description = "Maternal last name", example = "Lopez")
        @Size(max = NAME_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        @Pattern(regexp = ValidationMessages.NAME_PATTERN, message = ValidationMessages.INVALID_NAME)
        String maternalLastName,

        @Schema(description = "Age in years; must match the birth date", example = "31",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = ValidationMessages.REQUIRED)
        @Min(value = MIN_AGE, message = ValidationMessages.MIN_AGE)
        @Max(value = MAX_AGE, message = ValidationMessages.MAX_AGE)
        Integer age,

        @Schema(description = "Gender", example = "MALE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = ValidationMessages.REQUIRED)
        Gender gender,

        @Schema(description = "Birth date (dd-MM-yyyy)", type = "string", pattern = "\\d{2}-\\d{2}-\\d{4}",
                example = "15-03-1995", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = ValidationMessages.REQUIRED)
        @Past(message = ValidationMessages.PAST_DATE)
        @JsonFormat(pattern = EmployeeConstants.DATE_FORMAT)
        LocalDate birthDate,

        @Schema(description = "Job position", example = "Developer", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = ValidationMessages.REQUIRED)
        @Size(max = POSITION_MAX_LENGTH, message = ValidationMessages.MAX_LENGTH)
        String position,

        @Schema(description = "Whether the employee is active; defaults to true", example = "true")
        Boolean active) {
}
