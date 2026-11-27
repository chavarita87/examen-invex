package com.invex.examen.service;

import com.invex.examen.dto.EmployeeRequest;
import com.invex.examen.dto.EmployeeResponse;
import com.invex.examen.dto.EmployeeUpdateRequest;
import java.util.List;

/**
 * Business operations on employees.
 */
public interface EmployeeService {

    List<EmployeeResponse> findAll();

    EmployeeResponse findById(Long id);

    List<EmployeeResponse> createAll(List<EmployeeRequest> requests);

    EmployeeResponse update(Long id, EmployeeUpdateRequest request);

    void delete(Long id);

    List<EmployeeResponse> searchByName(String name);
}
