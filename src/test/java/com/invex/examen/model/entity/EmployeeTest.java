package com.invex.examen.model.entity;

import static com.invex.examen.support.EmployeeTestData.employee;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    @DisplayName("is equal to itself")
    void equalToItself() {
        Employee employee = employee(1L);

        assertThat(employee).isEqualTo(employee);
    }

    @Test
    @DisplayName("two employees with the same id are equal and share the hash code")
    void equalWhenSameId() {
        Employee first = employee(1L);
        Employee second = employee(1L);
        second.setFirstName("Other");

        assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
    }

    @Test
    @DisplayName("employees with different ids are not equal")
    void notEqualWhenDifferentId() {
        assertThat(employee(1L)).isNotEqualTo(employee(2L));
    }

    @Test
    @DisplayName("unsaved employees are never equal, even with the same data")
    void notEqualWhenIdIsNull() {
        assertThat(employee(null)).isNotEqualTo(employee(null));
    }

    @Test
    @DisplayName("is not equal to null or to another type")
    void notEqualToNullOrOtherType() {
        Employee employee = employee(1L);

        assertThat(employee).isNotEqualTo(null).isNotEqualTo("employee");
    }
}
