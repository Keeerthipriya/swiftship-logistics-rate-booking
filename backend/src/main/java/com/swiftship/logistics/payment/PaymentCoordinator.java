package com.swiftship.logistics.payment;

import com.swiftship.logistics.domain.PaymentMethod;
import com.swiftship.logistics.domain.exception.PaymentFailedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coordinates payment processing through registered PaymentProcessor strategies.
 * 
 * SOLID Principles:
 * - SRP: Isolates payment dispatching and error handling.
 * - DIP: Injects abstractions (PaymentProcessor) via constructor injection.
 * - OCP: New payment methods (e.g. PayPal, Apple Pay) are added by implementing
 *   PaymentProcessor without modifying this coordinator.
 */
@Service
public class PaymentCoordinator {

    private final Map<PaymentMethod, PaymentProcessor> processors = new ConcurrentHashMap<>();

    public PaymentCoordinator(List<PaymentProcessor> paymentProcessors) {
        if (paymentProcessors != null) {
            for (PaymentProcessor processor : paymentProcessors) {
                this.processors.put(processor.getMethod(), processor);
            }
        }
    }

    public void registerProcessor(PaymentProcessor processor) {
        this.processors.put(processor.getMethod(), processor);
    }

    public PaymentResult executePayment(PaymentMethod method, BigDecimal amount, String currency, String paymentReference) {
        PaymentProcessor processor = processors.get(method);
        if (processor == null) {
            throw new PaymentFailedException("No payment processor available for method: " + method);
        }

        PaymentResult result = processor.processPayment(amount, currency, paymentReference);
        if (!result.successful()) {
            throw new PaymentFailedException(
                "Payment declined: " + result.failureReason(),
                result.transactionId()
            );
        }

        return result;
    }
}
