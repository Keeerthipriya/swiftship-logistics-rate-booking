package com.swiftship.logistics.domain.exception;

/**
 * Thrown when a requested resource (e.g. Booking) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
