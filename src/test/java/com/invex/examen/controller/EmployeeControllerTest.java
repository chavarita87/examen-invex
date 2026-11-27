package com.invex.examen.controller;

import static com.invex.examen.support.EmployeeTestData.employee;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.invex.examen.config.SecurityConfig;
import com.invex.examen.constant.SecurityConstants;
import com.invex.examen.dto.EmployeeResponse;
import com.invex.examen.dto.EmployeeUpdateRequest;
import com.invex.examen.exception.BusinessValidationException;
import com.invex.examen.exception.EmployeeNotFoundException;
import com.invex.examen.exception.SecurityErrorHandler;
import com.invex.examen.mapper.EmployeeMapper;
import com.invex.examen.service.EmployeeService;
import com.invex.examen.support.EmployeeTestData;
import java.time.Clock;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
@ActiveProfiles("test")
@Import({EmployeeControllerTest.FixedClockConfig.class, SecurityConfig.class, SecurityErrorHandler.class})
@WithMockUser(roles = SecurityConstants.ROLE_ADMIN)
class EmployeeControllerTest {

    private static final String BASE_PATH = "/employees";
    private static final String VALID_EMPLOYEE = """
            {"firstName":"Juan","middleName":"Carlos","paternalLastName":"Perez","maternalLastName":"Lopez",
             "age":31,"gender":"MALE","birthDate":"15-03-1995","position":"Developer"}
            """;

    private final EmployeeMapper mapper = new EmployeeMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @TestConfiguration
    static class FixedClockConfig {

        @Bean
        Clock clock() {
            return EmployeeTestData.FIXED_CLOCK;
        }
    }

    /**
     * {@code @MockBean} is not reset between {@code @Nested} classes in Spring Boot 2.7,
     * so stubs and recorded calls would leak from one test to the next.
     */
    @AfterEach
    void resetMocks() {
        reset(service);
    }

    private EmployeeResponse response(Long id) {
        return mapper.toResponse(employee(id));
    }

    @Nested
    @DisplayName("GET /employees")
    class FindAll {

        @Test
        @DisplayName("returns 200 with the list and dates in dd-MM-yyyy")
        void returnsList() throws Exception {
            when(service.findAll()).thenReturn(List.of(response(1L), response(2L)));

            mockMvc.perform(get(BASE_PATH))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].birthDate").value("15-03-1995"))
                    .andExpect(jsonPath("$[0].createdAt").value("26-11-2026 10:30:00"));
        }
    }

    @Nested
    @DisplayName("GET /employees/{id}")
    class FindById {

        @Test
        @DisplayName("returns 200 with the employee")
        void returnsEmployee() throws Exception {
            when(service.findById(1L)).thenReturn(response(1L));

            mockMvc.perform(get(BASE_PATH + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Juan"));
        }

        @Test
        @DisplayName("returns 404 with the error body when the employee does not exist")
        void returnsNotFound() throws Exception {
            when(service.findById(99L)).thenThrow(new EmployeeNotFoundException(99L));

            mockMvc.perform(get(BASE_PATH + "/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Employee not found with id: 99"))
                    .andExpect(jsonPath("$.path").value("/employees/99"))
                    .andExpect(jsonPath("$.timestamp").value("26-11-2026 12:00:00"));
        }

        @Test
        @DisplayName("returns 400 when the id is not a number")
        void rejectsNonNumericId() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Parameter 'id' has an invalid value: abc"));
        }

        @Test
        @DisplayName("returns 400 when the id is not positive")
        void rejectsNegativeId() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/-5"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("id"))
                    .andExpect(jsonPath("$.fieldErrors[0].message").value("must be a positive number"));
        }
    }

    @Nested
    @DisplayName("POST /employees")
    class Create {

        @Test
        @DisplayName("returns 201 with the created employees")
        void createsEmployees() throws Exception {
            when(service.createAll(anyList())).thenReturn(List.of(response(1L), response(2L)));

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[" + VALID_EMPLOYEE + "," + VALID_EMPLOYEE + "]"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("returns 400 when the list is empty")
        void rejectsEmptyList() throws Exception {
            mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content("[]"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("requests"))
                    .andExpect(jsonPath("$.fieldErrors[0].message").value("must contain at least one employee"));
            verify(service, never()).createAll(anyList());
        }

        @Test
        @DisplayName("returns 400 naming each invalid field of each employee")
        void rejectsInvalidFields() throws Exception {
            String invalid = """
                    [{"firstName":"Ana1","age":15,"gender":"FEMALE","birthDate":"01-01-2099"}]
                    """;

            mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(invalid))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.fieldErrors", hasSize(5)))
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("requests[0].age"))
                    .andExpect(jsonPath("$.fieldErrors[1].field").value("requests[0].birthDate"))
                    .andExpect(jsonPath("$.fieldErrors[2].field").value("requests[0].firstName"))
                    .andExpect(jsonPath("$.fieldErrors[3].field").value("requests[0].paternalLastName"))
                    .andExpect(jsonPath("$.fieldErrors[4].field").value("requests[0].position"));
            verify(service, never()).createAll(anyList());
        }

        @Test
        @DisplayName("returns 400 when the date does not use dd-MM-yyyy")
        void rejectsWrongDateFormat() throws Exception {
            String wrongDate = VALID_EMPLOYEE.replace("15-03-1995", "1995-03-15");

            mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content("[" + wrongDate + "]"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("dd-MM-yyyy")));
        }

        @Test
        @DisplayName("returns 400 when the service rejects a business rule")
        void returnsBusinessError() throws Exception {
            when(service.createAll(anyList())).thenThrow(new BusinessValidationException("age mismatch"));

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[" + VALID_EMPLOYEE + "]"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("age mismatch"));
        }

        @Test
        @DisplayName("returns 409 when the data conflicts with existing records")
        void returnsConflict() throws Exception {
            when(service.createAll(anyList())).thenThrow(new DataIntegrityViolationException("duplicate"));

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[" + VALID_EMPLOYEE + "]"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("The operation conflicts with existing data"));
        }

        @Test
        @DisplayName("returns 500 without internal details on unexpected errors")
        void hidesUnexpectedErrors() throws Exception {
            when(service.createAll(anyList())).thenThrow(new IllegalStateException("database password leaked"));

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("[" + VALID_EMPLOYEE + "]"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }

    @Nested
    @DisplayName("PUT /employees/{id}")
    class Update {

        @Test
        @DisplayName("returns 200 and passes only the sent fields to the service")
        void updatesEmployee() throws Exception {
            when(service.update(eq(1L), any(EmployeeUpdateRequest.class))).thenReturn(response(1L));

            mockMvc.perform(put(BASE_PATH + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"position\":\"Senior Developer\"}"))
                    .andExpect(status().isOk());
            verify(service).update(1L, EmployeeUpdateRequest.builder().position("Senior Developer").build());
        }

        @Test
        @DisplayName("returns 400 when a sent field is invalid")
        void rejectsInvalidField() throws Exception {
            mockMvc.perform(put(BASE_PATH + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firstName\":\"\",\"position\":\"   \"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors", hasSize(2)))
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("firstName"))
                    .andExpect(jsonPath("$.fieldErrors[1].field").value("position"));
            verify(service, never()).update(any(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /employees/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204 when the employee is deleted")
        void deletesEmployee() throws Exception {
            mockMvc.perform(delete(BASE_PATH + "/1"))
                    .andExpect(status().isNoContent());
            verify(service).delete(1L);
        }

        @Test
        @DisplayName("returns 404 when the employee does not exist")
        void returnsNotFound() throws Exception {
            doThrow(new EmployeeNotFoundException(99L)).when(service).delete(99L);

            mockMvc.perform(delete(BASE_PATH + "/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /employees/search")
    class Search {

        @Test
        @DisplayName("returns 200 with the matching employees")
        void returnsMatches() throws Exception {
            when(service.searchByName("jua")).thenReturn(List.of(response(1L)));

            mockMvc.perform(get(BASE_PATH + "/search").param("name", "jua"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("returns 400 when the name parameter is missing")
        void rejectsMissingName() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/search"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Parameter 'name' is required"));
        }

        @Test
        @DisplayName("returns 400 when the name parameter is blank")
        void rejectsBlankName() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/search").param("name", " "))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
        }
    }

    @Test
    @DisplayName("returns 405 with the error body for unsupported methods")
    void rejectsUnsupportedMethod() throws Exception {
        mockMvc.perform(patch(BASE_PATH + "/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }
}
