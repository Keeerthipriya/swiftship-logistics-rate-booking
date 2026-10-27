package com.swiftship.logistics.payment;

import com.swiftship.logistics.domain.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Simulates Credit Card authorization and capture.
 */
@Component
public class CreditCardPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount, String currency, String paymentReference) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Invalid transaction amount: " + amount);
        }

        // Simulate successful card processing
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String txnId = "TXN-CC-" + suffix;
        String authCode = "AUTH-" + (100000 + (int)(Math.random() * 900000));

        return PaymentResult.success(txnId, authCode);
    }
}
