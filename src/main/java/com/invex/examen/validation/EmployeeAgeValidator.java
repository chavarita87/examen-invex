package com.invex.examen.validation;

import com.invex.examen.constant.EmployeeConstants;
import com.invex.examen.constant.ValidationMessages;
import com.invex.examen.exception.BusinessValidationException;
import com.invex.examen.model.entity.Employee;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Checks that the age of an employee matches the age derived from the birth date.
 */
@Component
@RequiredArgsConstructor
public class EmployeeAgeValidator {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(EmployeeConstants.DATE_FORMAT);

    private final Clock clock;

    public void validate(Employee employee) {
        int expectedAge = Period.between(employee.getBirthDate(), LocalDate.now(clock)).getYears();
        if (employee.getAge() != expectedAge) {
            throw new BusinessValidationException(String.format(ValidationMessages.AGE_MISMATCH,
                    employee.getAge(), employee.getBirthDate().format(DATE_FORMATTER), expectedAge));
        }
    }
}
