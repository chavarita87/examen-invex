package com.invex.examen.controller;

import com.invex.examen.constant.ApiConstants;
import com.invex.examen.dto.EmployeeRequest;
import com.invex.examen.dto.EmployeeResponse;
import com.invex.examen.dto.EmployeeUpdateRequest;
import com.invex.examen.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for employee management. Validation rules and API
 * documentation are declared on {@link EmployeeApi}.
 */
@Validated
@RestController
@RequestMapping(ApiConstants.EMPLOYEES_PATH)
@RequiredArgsConstructor
public class EmployeeController implements EmployeeApi {

    private final EmployeeService service;

    @Override
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Override
    @GetMapping(ApiConstants.ID_PATH)
    public ResponseEntity<EmployeeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Override
    @PostMapping
    public ResponseEntity<List<EmployeeResponse>> create(@RequestBody List<EmployeeRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAll(requests));
    }

    @Override
    @PutMapping(ApiConstants.ID_PATH)
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id,
                                                   @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Override
    @DeleteMapping(ApiConstants.ID_PATH)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping(ApiConstants.SEARCH_PATH)
    public ResponseEntity<List<EmployeeResponse>> search(@RequestParam(ApiConstants.NAME_PARAM) String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }
}
