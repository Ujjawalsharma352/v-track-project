package com.vtrack.exception;

/**
 * Exception for business rule violations (e.g. vehicle already IN).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
