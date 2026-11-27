package com.invex.examen.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.invex.examen.model.entity.Employee;
import com.invex.examen.model.enums.Gender;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.saveAll(List.of(
                employee("Juan", "Carlos", "Perez", "Lopez"),
                employee("Maria", null, "Gonzalez", "Juarez"),
                employee("Pedro", null, "Ramirez", null)));
    }

    @Test
    @DisplayName("save assigns id, creation timestamp and active flag by default")
    void saveAssignsGeneratedValues() {
        Employee saved = repository.save(employee("Ana", null, "Torres", "Diaz"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getActive()).isTrue();
    }

    @Test
    @DisplayName("searchByName matches partially and ignores case")
    void searchByNameIsPartialAndCaseInsensitive() {
        List<Employee> result = repository.searchByName("JUA");

        assertThat(result)
                .extracting(Employee::getFirstName)
                .containsExactly("Maria", "Juan");
    }

    @Test
    @DisplayName("searchByName looks in middle name and last names")
    void searchByNameCoversAllNameFields() {
        assertThat(repository.searchByName("carl")).extracting(Employee::getFirstName).containsExactly("Juan");
        assertThat(repository.searchByName("ramir")).extracting(Employee::getFirstName).containsExactly("Pedro");
    }

    @Test
    @DisplayName("searchByName returns an empty list when nothing matches")
    void searchByNameWithoutMatches() {
        assertThat(repository.searchByName("xyz")).isEmpty();
    }

    private static Employee employee(String firstName, String middleName, String paternal, String maternal) {
        return Employee.builder()
                .firstName(firstName)
                .middleName(middleName)
                .paternalLastName(paternal)
                .maternalLastName(maternal)
                .age(30)
                .gender(Gender.OTHER)
                .birthDate(LocalDate.of(1995, 1, 15))
                .position("Developer")
                .build();
    }
}
