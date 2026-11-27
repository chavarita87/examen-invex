package com.invex.examen.exception;

/**
 * Thrown when a request is well-formed but breaks a business rule.
 */
public class BusinessValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessValidationException(String message) {
        super(message);
    }
}
