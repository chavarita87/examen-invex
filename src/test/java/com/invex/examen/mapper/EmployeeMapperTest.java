package com.invex.examen.mapper;

import static com.invex.examen.support.EmployeeTestData.employee;
import static com.invex.examen.support.EmployeeTestData.request;
import static org.assertj.core.api.Assertions.assertThat;

import com.invex.examen.dto.EmployeeRequest;
import com.invex.examen.dto.EmployeeResponse;
import com.invex.examen.dto.EmployeeUpdateRequest;
import com.invex.examen.model.entity.Employee;
import com.invex.examen.model.enums.Gender;
import com.invex.examen.support.EmployeeTestData;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = new EmployeeMapper();

    @Test
    @DisplayName("toEntity copies every field and marks the employee active by default")
    void toEntityDefaultsActive() {
        Employee entity = mapper.toEntity(request());

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("Juan");
        assertThat(entity.getMiddleName()).isEqualTo("Carlos");
        assertThat(entity.getPaternalLastName()).isEqualTo("Perez");
        assertThat(entity.getMaternalLastName()).isEqualTo("Lopez");
        assertThat(entity.getAge()).isEqualTo(EmployeeTestData.AGE);
        assertThat(entity.getGender()).isEqualTo(Gender.MALE);
        assertThat(entity.getBirthDate()).isEqualTo(EmployeeTestData.BIRTH_DATE);
        assertThat(entity.getPosition()).isEqualTo("Developer");
        assertThat(entity.getActive()).isTrue();
    }

    @Test
    @DisplayName("toEntity keeps the active flag when it is sent")
    void toEntityKeepsActive() {
        EmployeeRequest request = EmployeeRequest.builder().active(Boolean.FALSE).build();

        assertThat(mapper.toEntity(request).getActive()).isFalse();
    }

    @Test
    @DisplayName("toResponse copies every field, including id and creation timestamp")
    void toResponseCopiesFields() {
        EmployeeResponse response = mapper.toResponse(employee(7L));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.firstName()).isEqualTo("Juan");
        assertThat(response.paternalLastName()).isEqualTo("Perez");
        assertThat(response.createdAt()).isEqualTo(EmployeeTestData.CREATED_AT);
        assertThat(response.active()).isTrue();
    }

    @Test
    @DisplayName("updateEntity changes only the fields present in the request")
    void updateEntityIsPartial() {
        Employee entity = employee(1L);
        EmployeeUpdateRequest request = EmployeeUpdateRequest.builder()
                .middleName("Antonio")
                .gender(Gender.OTHER)
                .build();

        mapper.updateEntity(entity, request);

        assertThat(entity.getMiddleName()).isEqualTo("Antonio");
        assertThat(entity.getGender()).isEqualTo(Gender.OTHER);
        assertThat(entity.getFirstName()).isEqualTo("Juan");
        assertThat(entity.getPosition()).isEqualTo("Developer");
        assertThat(entity.getActive()).isTrue();
    }

    @Test
    @DisplayName("updateEntity can change every field at once")
    void updateEntityChangesAllFields() {
        Employee entity = employee(1L);
        LocalDate newBirthDate = LocalDate.of(1990, 1, 1);
        EmployeeUpdateRequest request = new EmployeeUpdateRequest("Ana", "Maria", "Diaz", "Ruiz", 36,
                Gender.FEMALE, newBirthDate, "Manager", Boolean.FALSE);

        mapper.updateEntity(entity, request);

        assertThat(entity)
                .extracting(Employee::getFirstName, Employee::getMiddleName, Employee::getPaternalLastName,
                        Employee::getMaternalLastName, Employee::getAge, Employee::getGender,
                        Employee::getBirthDate, Employee::getPosition, Employee::getActive)
                .containsExactly("Ana", "Maria", "Diaz", "Ruiz", 36, Gender.FEMALE, newBirthDate, "Manager",
                        Boolean.FALSE);
    }
}
