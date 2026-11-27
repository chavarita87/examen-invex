package com.invex.examen.support;

import com.invex.examen.dto.EmployeeRequest;
import com.invex.examen.model.entity.Employee;
import com.invex.examen.model.enums.Gender;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Shared fixtures for employee tests.
 */
public final class EmployeeTestData {

    public static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-11-26T12:00:00Z"), ZoneOffset.UTC);
    public static final LocalDate BIRTH_DATE = LocalDate.of(1995, 3, 15);
    public static final int AGE = 31;
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 11, 26, 10, 30, 0);

    private EmployeeTestData() {
    }

    public static Employee employee(Long id) {
        return Employee.builder()
                .id(id)
                .firstName("Juan")
                .middleName("Carlos")
                .paternalLastName("Perez")
                .maternalLastName("Lopez")
                .age(AGE)
                .gender(Gender.MALE)
                .birthDate(BIRTH_DATE)
                .position("Developer")
                .createdAt(CREATED_AT)
                .active(Boolean.TRUE)
                .build();
    }

    public static EmployeeRequest request() {
        return EmployeeRequest.builder()
                .firstName("Juan")
                .middleName("Carlos")
                .paternalLastName("Perez")
                .maternalLastName("Lopez")
                .age(AGE)
                .gender(Gender.MALE)
                .birthDate(BIRTH_DATE)
                .position("Developer")
                .build();
    }
}
