package com.invex.examen.mapper;

import com.invex.examen.dto.EmployeeRequest;
import com.invex.examen.dto.EmployeeResponse;
import com.invex.examen.dto.EmployeeUpdateRequest;
import com.invex.examen.model.entity.Employee;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

/**
 * Converts between the {@link Employee} entity and the API DTOs.
 */
@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        return Employee.builder()
                .firstName(request.firstName())
                .middleName(request.middleName())
                .paternalLastName(request.paternalLastName())
                .maternalLastName(request.maternalLastName())
                .age(request.age())
                .gender(request.gender())
                .birthDate(request.birthDate())
                .position(request.position())
                .active(Optional.ofNullable(request.active()).orElse(Boolean.TRUE))
                .build();
    }

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .middleName(employee.getMiddleName())
                .paternalLastName(employee.getPaternalLastName())
                .maternalLastName(employee.getMaternalLastName())
                .age(employee.getAge())
                .gender(employee.getGender())
                .birthDate(employee.getBirthDate())
                .position(employee.getPosition())
                .createdAt(employee.getCreatedAt())
                .active(employee.getActive())
                .build();
    }

    /**
     * Copies onto the entity only the fields present in the request.
     */
    public void updateEntity(Employee employee, EmployeeUpdateRequest request) {
        setIfPresent(request.firstName(), employee::setFirstName);
        setIfPresent(request.middleName(), employee::setMiddleName);
        setIfPresent(request.paternalLastName(), employee::setPaternalLastName);
        setIfPresent(request.maternalLastName(), employee::setMaternalLastName);
        setIfPresent(request.age(), employee::setAge);
        setIfPresent(request.gender(), employee::setGender);
        setIfPresent(request.birthDate(), employee::setBirthDate);
        setIfPresent(request.position(), employee::setPosition);
        setIfPresent(request.active(), employee::setActive);
    }

    private static <T> void setIfPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }
}
