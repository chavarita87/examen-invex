package com.invex.examen.validation;

import static com.invex.examen.support.EmployeeTestData.employee;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.invex.examen.exception.BusinessValidationException;
import com.invex.examen.model.entity.Employee;
import com.invex.examen.support.EmployeeTestData;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmployeeAgeValidatorTest {

    private final EmployeeAgeValidator validator = new EmployeeAgeValidator(EmployeeTestData.FIXED_CLOCK);

    @Test
    @DisplayName("accepts an age that matches the birth date")
    void acceptsMatchingAge() {
        Employee employee = employee(1L);

        assertThatCode(() -> validator.validate(employee)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "born {0}, age {1} is valid on 2026-11-26")
    @CsvSource({
        "1995-11-26, 31",
        "1995-11-27, 30",
        "2008-11-26, 18"
    })
    @DisplayName("counts a birthday only once it has been reached")
    void handlesBirthdayBoundaries(LocalDate birthDate, int age) {
        Employee employee = employee(1L);
        employee.setBirthDate(birthDate);
        employee.setAge(age);

        assertThatCode(() -> validator.validate(employee)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("rejects an age that does not match the birth date and explains why")
    void rejectsMismatchedAge() {
        Employee employee = employee(1L);
        employee.setAge(40);

        assertThatThrownBy(() -> validator.validate(employee))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("age 40 does not match birth date 15-03-1995 (expected 31)");
    }
}
