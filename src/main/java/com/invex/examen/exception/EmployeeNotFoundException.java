package com.invex.examen.exception;

/**
 * Thrown when no employee exists for the requested id.
 */
public class EmployeeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Employee not found with id: %d";

    public EmployeeNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
