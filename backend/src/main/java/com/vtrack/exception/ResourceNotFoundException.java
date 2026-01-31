package com.vtrack.exception;

/**
 * Exception thrown when a resource (e.g. vehicle log) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
