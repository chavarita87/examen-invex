package com.invex.examen.security;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.invex.examen.service.EmployeeService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Checks authentication and role rules with the real security configuration and the test users
 * defined in application-test.yml.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeSecurityTest {

    private static final String READER = "reader";
    private static final String READER_PASSWORD = "reader-test-pass";
    private static final String ADMIN = "admin";
    private static final String ADMIN_PASSWORD = "admin-test-pass";
    private static final String VALID_EMPLOYEE = """
            [{"firstName":"Juan","paternalLastName":"Perez","age":31,"gender":"MALE",
              "birthDate":"15-03-1995","position":"Developer"}]
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Test
    @DisplayName("rejects requests without credentials with 401, a Basic challenge and the error body")
    void rejectsAnonymous() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"employees\""))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required to access this resource"))
                .andExpect(jsonPath("$.path").value("/employees"));
    }

    @Test
    @DisplayName("rejects a wrong password with 401")
    void rejectsWrongPassword() throws Exception {
        mockMvc.perform(get("/employees").with(httpBasic(ADMIN, "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("lets the reader list and search employees")
    void readerCanRead() throws Exception {
        when(service.findAll()).thenReturn(List.of());
        when(service.searchByName("jua")).thenReturn(List.of());

        mockMvc.perform(get("/employees").with(httpBasic(READER, READER_PASSWORD)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/employees/search").param("name", "jua").with(httpBasic(READER, READER_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("forbids the reader from creating, updating or deleting with 403")
    void readerCannotWrite() throws Exception {
        mockMvc.perform(post("/employees").with(httpBasic(READER, READER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_EMPLOYEE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("You do not have permission to perform this operation"));
        mockMvc.perform(put("/employees/1").with(httpBasic(READER, READER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"position\":\"QA\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/employees/1").with(httpBasic(READER, READER_PASSWORD)))
                .andExpect(status().isForbidden());

        verify(service, never()).createAll(anyList());
    }

    @Test
    @DisplayName("lets the admin create employees")
    void adminCanWrite() throws Exception {
        when(service.createAll(anyList())).thenReturn(List.of());

        mockMvc.perform(post("/employees").with(httpBasic(ADMIN, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_EMPLOYEE))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("keeps health, info and the API docs public")
    void publicEndpoints() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/info")).andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs.yaml")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("denies any path that is not explicitly allowed, even to the admin")
    void deniesUnknownPaths() throws Exception {
        mockMvc.perform(get("/actuator/env").with(httpBasic(ADMIN, ADMIN_PASSWORD)))
                .andExpect(status().isForbidden());
    }
}
