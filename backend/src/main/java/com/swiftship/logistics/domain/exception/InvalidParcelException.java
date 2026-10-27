package com.swiftship.logistics.domain.exception;

/**
 * Thrown when parcel parameters fail physical constraints:
 * - Weight must be between 0.1 kg and 50.0 kg.
 * - Distance must be between 1 km and 2,500 km.
 */
public class InvalidParcelException extends RuntimeException {

    public InvalidParcelException(String message) {
        super(message);
    }
}
