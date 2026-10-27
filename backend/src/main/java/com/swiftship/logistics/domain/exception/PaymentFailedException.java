package com.swiftship.logistics.domain.exception;

/**
 * Thrown when payment authorization fails.
 */
public class PaymentFailedException extends RuntimeException {

    private final String transactionId;

    public PaymentFailedException(String message) {
        super(message);
        this.transactionId = null;
    }

    public PaymentFailedException(String message, String transactionId) {
        super(message);
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
