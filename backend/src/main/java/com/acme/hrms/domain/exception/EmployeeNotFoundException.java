package com.acme.hrms.domain.exception;

import java.util.UUID;

/**
 * Thrown when an employee lookup fails.
 */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(UUID id) {
        super("Employee not found with ID: " + id);
    }

    public EmployeeNotFoundException(String identifier) {
        super("Employee not found with identifier: " + identifier);
    }
}

