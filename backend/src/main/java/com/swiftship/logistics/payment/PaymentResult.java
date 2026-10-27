package com.swiftship.logistics.payment;

import java.time.Instant;

/**
 * Result of a simulated payment transaction.
 */
public record PaymentResult(
    boolean successful,
    String transactionId,
    String authorizationCode,
    String failureReason,
    Instant timestamp
) {
    public static PaymentResult success(String transactionId, String authorizationCode) {
        return new PaymentResult(true, transactionId, authorizationCode, null, Instant.now());
    }

    public static PaymentResult failure(String failureReason) {
        return new PaymentResult(false, null, null, failureReason, Instant.now());
    }
}
