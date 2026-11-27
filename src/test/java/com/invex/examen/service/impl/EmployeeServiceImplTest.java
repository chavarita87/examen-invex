package com.invex.examen.service.impl;

import static com.invex.examen.support.EmployeeTestData.employee;
import static com.invex.examen.support.EmployeeTestData.request;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.invex.examen.dto.EmployeeRequest;
import com.invex.examen.dto.EmployeeResponse;
import com.invex.examen.dto.EmployeeUpdateRequest;
import com.invex.examen.exception.BusinessValidationException;
import com.invex.examen.exception.EmployeeNotFoundException;
import com.invex.examen.mapper.EmployeeMapper;
import com.invex.examen.model.entity.Employee;
import com.invex.examen.repository.EmployeeRepository;
import com.invex.examen.validation.EmployeeAgeValidator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    private static final Long ID = 1L;

    @Mock
    private EmployeeRepository repository;

    @Spy
    private EmployeeMapper mapper;

    @Mock
    private EmployeeAgeValidator ageValidator;

    private EmployeeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmployeeServiceImpl(repository, mapper, ageValidator);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("returns every employee mapped to a response")
        void returnsAllEmployees() {
            when(repository.findAll()).thenReturn(List.of(employee(1L), employee(2L)));

            List<EmployeeResponse> result = service.findAll();

            assertThat(result).extracting(EmployeeResponse::id).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("returns an empty list when there are no employees")
        void returnsEmptyList() {
            when(repository.findAll()).thenReturn(List.of());

            assertThat(service.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns the employee when it exists")
        void returnsEmployee() {
            when(repository.findById(ID)).thenReturn(Optional.of(employee(ID)));

            EmployeeResponse result = service.findById(ID);

            assertThat(result.id()).isEqualTo(ID);
            assertThat(result.firstName()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("throws EmployeeNotFoundException when it does not exist")
        void throwsWhenMissing() {
            when(repository.findById(ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(ID))
                    .isInstanceOf(EmployeeNotFoundException.class)
                    .hasMessageContaining(String.valueOf(ID));
        }
    }

    @Nested
    @DisplayName("createAll")
    class CreateAll {

        @Test
        @DisplayName("validates and saves every employee in one call")
        @SuppressWarnings("unchecked")
        void savesAllEmployees() {
            List<EmployeeRequest> requests = List.of(request(), request());
            when(repository.saveAll(anyList())).thenReturn(List.of(employee(1L), employee(2L)));

            List<EmployeeResponse> result = service.createAll(requests);

            ArgumentCaptor<List<Employee>> captor = ArgumentCaptor.forClass(List.class);
            verify(repository).saveAll(captor.capture());
            assertThat(captor.getValue()).hasSize(2);
            captor.getValue().forEach(employee -> verify(ageValidator).validate(employee));
            assertThat(result).extracting(EmployeeResponse::id).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("saves nothing when one employee breaks the age rule")
        void doesNotSaveWhenValidationFails() {
            doThrow(new BusinessValidationException("age mismatch")).when(ageValidator).validate(any());

            List<EmployeeRequest> requests = List.of(request());
            assertThatThrownBy(() -> service.createAll(requests))
                    .isInstanceOf(BusinessValidationException.class);
            verify(repository, never()).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("changes only the fields sent in the request")
        void updatesOnlyGivenFields() {
            Employee existing = employee(ID);
            when(repository.findById(ID)).thenReturn(Optional.of(existing));
            when(repository.save(existing)).thenReturn(existing);
            EmployeeUpdateRequest request = EmployeeUpdateRequest.builder()
                    .position("Senior Developer")
                    .active(Boolean.FALSE)
                    .build();

            EmployeeResponse result = service.update(ID, request);

            assertThat(result.position()).isEqualTo("Senior Developer");
            assertThat(result.active()).isFalse();
            assertThat(result.firstName()).isEqualTo("Juan");
            verify(ageValidator).validate(existing);
        }

        @Test
        @DisplayName("throws EmployeeNotFoundException when the employee does not exist")
        void throwsWhenMissing() {
            when(repository.findById(ID)).thenReturn(Optional.empty());
            EmployeeUpdateRequest request = EmployeeUpdateRequest.builder().build();

            assertThatThrownBy(() -> service.update(ID, request))
                    .isInstanceOf(EmployeeNotFoundException.class);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("does not save when the result breaks the age rule")
        void doesNotSaveWhenValidationFails() {
            Employee existing = employee(ID);
            when(repository.findById(ID)).thenReturn(Optional.of(existing));
            doThrow(new BusinessValidationException("age mismatch")).when(ageValidator).validate(existing);
            EmployeeUpdateRequest request = EmployeeUpdateRequest.builder().age(50).build();

            assertThatThrownBy(() -> service.update(ID, request))
                    .isInstanceOf(BusinessValidationException.class);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes the employee when it exists")
        void deletesEmployee() {
            Employee existing = employee(ID);
            when(repository.findById(ID)).thenReturn(Optional.of(existing));

            service.delete(ID);

            verify(repository).delete(existing);
        }

        @Test
        @DisplayName("throws EmployeeNotFoundException when the employee does not exist")
        void throwsWhenMissing() {
            when(repository.findById(ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(ID))
                    .isInstanceOf(EmployeeNotFoundException.class);
            verify(repository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("searchByName")
    class SearchByName {

        @Test
        @DisplayName("trims the text before searching")
        void trimsName() {
            when(repository.searchByName("juan")).thenReturn(List.of(employee(ID)));

            List<EmployeeResponse> result = service.searchByName("  juan  ");

            assertThat(result).extracting(EmployeeResponse::id).containsExactly(ID);
            verify(repository).searchByName("juan");
        }
    }
}
