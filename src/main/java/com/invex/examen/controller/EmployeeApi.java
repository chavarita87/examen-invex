package com.invex.examen.controller;

import com.invex.examen.constant.EmployeeConstants;
import com.invex.examen.constant.ValidationMessages;
import com.invex.examen.dto.EmployeeRequest;
import com.invex.examen.dto.EmployeeResponse;
import com.invex.examen.dto.EmployeeUpdateRequest;
import com.invex.examen.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import org.springframework.http.ResponseEntity;

/**
 * Contract of the employee endpoints: OpenAPI documentation and input validation rules.
 * Keeping them here leaves {@link EmployeeController} with request handling only.
 * Parameter constraints must be declared on this interface and not on the implementation,
 * because Bean Validation does not allow an overriding method to redefine them.
 */
@Tag(name = "Employees", description = "Employee management")
@ApiResponse(responseCode = "401", description = EmployeeApi.UNAUTHORIZED,
        content = @Content(mediaType = EmployeeApi.JSON, schema = @Schema(implementation = ErrorResponse.class)))
public interface EmployeeApi {

    String JSON = "application/json";
    String BAD_REQUEST = "Invalid input: validation error, malformed body or broken business rule";
    String NOT_FOUND = "Employee not found";
    String SERVER_ERROR = "Unexpected error";
    String UNAUTHORIZED = "Missing or invalid credentials";
    String FORBIDDEN = "Authenticated user without the ADMIN role";

    String CREATE_EXAMPLE = """
            [
              {
                "firstName": "Juan",
                "middleName": "Carlos",
                "paternalLastName": "Perez",
                "maternalLastName": "Lopez",
                "age": 31,
                "gender": "MALE",
                "birthDate": "15-03-1995",
                "position": "Developer"
              },
              {
                "firstName": "Maria",
                "paternalLastName": "Gonzalez",
                "age": 28,
                "gender": "FEMALE",
                "birthDate": "02-07-1998",
                "position": "Analyst",
                "active": false
              }
            ]
            """;

    String UPDATE_EXAMPLE = """
            {
              "position": "Senior Developer",
              "active": false
            }
            """;

    String VALIDATION_ERROR_EXAMPLE = """
            {
              "timestamp": "26-11-2026 18:42:42",
              "status": 400,
              "error": "Bad Request",
              "message": "Validation failed",
              "path": "/employees",
              "fieldErrors": [
                { "field": "requests[0].age", "message": "must be at least 18" },
                { "field": "requests[0].paternalLastName", "message": "is required" }
              ]
            }
            """;

    String NOT_FOUND_EXAMPLE = """
            {
              "timestamp": "26-11-2026 18:42:42",
              "status": 404,
              "error": "Not Found",
              "message": "Employee not found with id: 99",
              "path": "/employees/99"
            }
            """;

    @Operation(summary = "List employees", description = "Returns every registered employee.")
    @ApiResponse(responseCode = "200", description = "Employees found",
            content = @Content(mediaType = JSON,
                    array = @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class))))
    @ApiResponse(responseCode = "500", description = SERVER_ERROR,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<List<EmployeeResponse>> findAll();

    @Operation(summary = "Get an employee", description = "Returns the detail of one employee by id.")
    @ApiResponse(responseCode = "200", description = "Employee found",
            content = @Content(mediaType = JSON, schema = @Schema(implementation = EmployeeResponse.class)))
    @ApiResponse(responseCode = "400", description = "The id is not a positive number",
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = NOT_FOUND,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = NOT_FOUND_EXAMPLE)))
    ResponseEntity<EmployeeResponse> findById(
            @Parameter(description = "Employee id", example = "1")
            @Positive(message = ValidationMessages.POSITIVE_ID) Long id);

    @Operation(summary = "Create employees",
            description = "Creates one or more employees in a single request (1 to 100). "
                    + "Either all of them are saved or none.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(mediaType = JSON,
                            array = @ArraySchema(schema = @Schema(implementation = EmployeeRequest.class)),
                            examples = @ExampleObject(name = "Two employees", value = CREATE_EXAMPLE))))
    @ApiResponse(responseCode = "201", description = "Employees created",
            content = @Content(mediaType = JSON,
                    array = @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class))))
    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = VALIDATION_ERROR_EXAMPLE)))
    @ApiResponse(responseCode = "409", description = "The data conflicts with existing records",
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = FORBIDDEN,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<List<EmployeeResponse>> create(
            @NotEmpty(message = ValidationMessages.EMPTY_LIST)
            @Size(max = EmployeeConstants.MAX_BATCH_SIZE, message = ValidationMessages.LIST_SIZE)
            List<@NotNull(message = ValidationMessages.REQUIRED) @Valid EmployeeRequest> requests);

    @Operation(summary = "Update an employee",
            description = "Updates some or all fields. Only the fields sent are changed; "
                    + "age and birth date must stay consistent.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(mediaType = JSON,
                            schema = @Schema(implementation = EmployeeUpdateRequest.class),
                            examples = @ExampleObject(name = "Change position", value = UPDATE_EXAMPLE))))
    @ApiResponse(responseCode = "200", description = "Employee updated",
            content = @Content(mediaType = JSON, schema = @Schema(implementation = EmployeeResponse.class)))
    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = NOT_FOUND,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = FORBIDDEN,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<EmployeeResponse> update(
            @Parameter(description = "Employee id", example = "1")
            @Positive(message = ValidationMessages.POSITIVE_ID) Long id,
            @Valid EmployeeUpdateRequest request);

    @Operation(summary = "Delete an employee", description = "Deletes one employee by id.")
    @ApiResponse(responseCode = "204", description = "Employee deleted")
    @ApiResponse(responseCode = "400", description = "The id is not a positive number",
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = NOT_FOUND,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = FORBIDDEN,
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<Void> delete(
            @Parameter(description = "Employee id", example = "1")
            @Positive(message = ValidationMessages.POSITIVE_ID) Long id);

    @Operation(summary = "Search employees by name",
            description = "Case-insensitive partial search on first name, middle name and both last names.")
    @ApiResponse(responseCode = "200", description = "Matching employees (may be empty)",
            content = @Content(mediaType = JSON,
                    array = @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class))))
    @ApiResponse(responseCode = "400", description = "The name is missing or blank",
            content = @Content(mediaType = JSON, schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<List<EmployeeResponse>> search(
            @Parameter(description = "Text to look for", example = "jua")
            @NotBlank(message = ValidationMessages.NOT_BLANK) String name);
}
