package com.swiftship.logistics.payment;

import com.swiftship.logistics.domain.PaymentMethod;
import com.swiftship.logistics.domain.exception.PaymentFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentProcessorTests {

    private PaymentCoordinator paymentCoordinator;

    @BeforeEach
    void setUp() {
        paymentCoordinator = new PaymentCoordinator(List.of(
                new CreditCardPaymentProcessor(),
                new CorporateAccountPaymentProcessor()
        ));
    }

    @Test
    @DisplayName("Credit Card payment simulation generates valid transaction ID")
    void testCreditCardPaymentSuccess() {
        PaymentResult result = paymentCoordinator.executePayment(
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("45.50"),
                "USD",
                "4111222233334444"
        );

        assertThat(result.successful()).isTrue();
        assertThat(result.transactionId()).startsWith("TXN-CC-");
        assertThat(result.authorizationCode()).isNotNull();
    }

    @Test
    @DisplayName("Corporate Account payment simulation generates valid transaction ID")
    void testCorporateAccountPaymentSuccess() {
        PaymentResult result = paymentCoordinator.executePayment(
                PaymentMethod.CORPORATE_ACCOUNT,
                new BigDecimal("120.00"),
                "USD",
                "CORP-ACCT-9842"
        );

        assertThat(result.successful()).isTrue();
        assertThat(result.transactionId()).startsWith("TXN-CORP-");
        assertThat(result.authorizationCode()).isNotNull();
    }

    @Test
    @DisplayName("Payment fails predictably when transaction amount is zero or negative")
    void testInvalidPaymentAmount() {
        assertThatThrownBy(() -> paymentCoordinator.executePayment(
                PaymentMethod.CREDIT_CARD,
                BigDecimal.ZERO,
                "USD",
                null
        )).isInstanceOf(PaymentFailedException.class)
          .hasMessageContaining("Payment declined");
    }
}
