package com.invex.examen.service.impl;

import com.invex.examen.dto.EmployeeRequest;
import com.invex.examen.dto.EmployeeResponse;
import com.invex.examen.dto.EmployeeUpdateRequest;
import com.invex.examen.exception.EmployeeNotFoundException;
import com.invex.examen.mapper.EmployeeMapper;
import com.invex.examen.model.entity.Employee;
import com.invex.examen.repository.EmployeeRepository;
import com.invex.examen.service.EmployeeService;
import com.invex.examen.validation.EmployeeAgeValidator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link EmployeeService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;
    private final EmployeeAgeValidator ageValidator;

    @Override
    public List<EmployeeResponse> findAll() {
        return toResponses(repository.findAll());
    }

    @Override
    public EmployeeResponse findById(Long id) {
        return mapper.toResponse(getEmployee(id));
    }

    @Override
    @Transactional
    public List<EmployeeResponse> createAll(List<EmployeeRequest> requests) {
        List<Employee> employees = requests.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        employees.forEach(ageValidator::validate);
        List<Employee> saved = repository.saveAll(employees);
        log.info("Created {} employee(s)", saved.size());
        return toResponses(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = getEmployee(id);
        mapper.updateEntity(employee, request);
        ageValidator.validate(employee);
        Employee saved = repository.save(employee);
        log.info("Updated employee {}", id);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = getEmployee(id);
        repository.delete(employee);
        log.info("Deleted employee {}", id);
    }

    @Override
    public List<EmployeeResponse> searchByName(String name) {
        return toResponses(repository.searchByName(name.trim()));
    }

    private Employee getEmployee(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private List<EmployeeResponse> toResponses(List<Employee> employees) {
        return employees.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
