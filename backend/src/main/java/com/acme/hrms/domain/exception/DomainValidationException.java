package com.acme.hrms.domain.exception;

/**
 * Thrown when domain business invariants or validations are violated.
 */
public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}

