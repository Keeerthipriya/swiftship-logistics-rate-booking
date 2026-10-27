package com.swiftship.logistics.payment;

import com.swiftship.logistics.domain.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Simulates Corporate Account Billing and credit limit verification.
 */
@Component
public class CorporateAccountPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.CORPORATE_ACCOUNT;
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount, String currency, String paymentReference) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Invalid transaction amount: " + amount);
        }

        // Simulate corporate credit approval
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String txnId = "TXN-CORP-" + suffix;
        String authCode = "CORP-INV-" + (500000 + (int)(Math.random() * 500000));

        return PaymentResult.success(txnId, authCode);
    }
}
