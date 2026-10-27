package com.swiftship.logistics.payment;

import com.swiftship.logistics.domain.PaymentMethod;

import java.math.BigDecimal;

/**
 * Abstraction for payment gateways (DIP & Strategy Pattern).
 * High-level booking orchestrators depend on this abstraction rather than concrete payment SDKs.
 */
public interface PaymentProcessor {

    /**
     * The payment method handled by this processor.
     */
    PaymentMethod getMethod();

    /**
     * Determines whether this processor handles the specified payment method.
     */
    default boolean supports(PaymentMethod method) {
        return getMethod() == method;
    }

    /**
     * Processes payment authorization.
     * 
     * @param amount Transaction amount
     * @param currency 3-letter currency code (e.g. USD)
     * @param paymentReference Optional card number or account reference
     * @return PaymentResult indicating success/failure and transaction ID
     */
    PaymentResult processPayment(BigDecimal amount, String currency, String paymentReference);
}
