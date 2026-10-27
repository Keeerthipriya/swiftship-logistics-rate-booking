package com.swiftship.logistics.rate;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;
import com.swiftship.logistics.domain.exception.InvalidParcelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateCalculationTests {

    private RateCalculationCoordinator coordinator;

    @BeforeEach
    void setUp() {
        coordinator = new RateCalculationCoordinator(List.of(
                new StandardRateCalculator(),
                new ExpressRateCalculator(),
                new SameDayRateCalculator()
        ));
    }

    @Test
    @DisplayName("STANDARD rate formula: $5.00 base + $1.20/kg + $0.05/km")
    void testStandardRateCalculation() {
        // 10.0 kg, 200.0 km
        // Base: $5.00, WeightFee: 10 * 1.20 = $12.00, DistFee: 200 * 0.05 = $10.00
        // Expected total: $27.00
        ParcelQuote quote = coordinator.calculateRate(ShippingTier.STANDARD, 10.0, 200.0);

        assertThat(quote.baseFee()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(quote.weightFee()).isEqualByComparingTo(new BigDecimal("12.00"));
        assertThat(quote.distanceFee()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(quote.totalCost()).isEqualByComparingTo(new BigDecimal("27.00"));
        assertThat(quote.estimatedDelivery()).isEqualTo("3–5 business days");
    }

    @Test
    @DisplayName("EXPRESS rate formula: $15.00 base + $2.50/kg + $0.12/km")
    void testExpressRateCalculation() {
        // 4.0 kg, 50.0 km
        // Base: $15.00, WeightFee: 4 * 2.50 = $10.00, DistFee: 50 * 0.12 = $6.00
        // Expected total: $31.00
        ParcelQuote quote = coordinator.calculateRate(ShippingTier.EXPRESS, 4.0, 50.0);

        assertThat(quote.baseFee()).isEqualByComparingTo(new BigDecimal("15.00"));
        assertThat(quote.weightFee()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(quote.distanceFee()).isEqualByComparingTo(new BigDecimal("6.00"));
        assertThat(quote.totalCost()).isEqualByComparingTo(new BigDecimal("31.00"));
        assertThat(quote.estimatedDelivery()).isEqualTo("Next day");
    }

    @Test
    @DisplayName("SAME_DAY rate formula: $35.00 base + $4.00/kg + $0.25/km (<= 100km)")
    void testSameDayRateCalculation() {
        // 2.5 kg, 40.0 km
        // Base: $35.00, WeightFee: 2.5 * 4.00 = $10.00, DistFee: 40 * 0.25 = $10.00
        // Expected total: $55.00
        ParcelQuote quote = coordinator.calculateRate(ShippingTier.SAME_DAY, 2.5, 40.0);

        assertThat(quote.baseFee()).isEqualByComparingTo(new BigDecimal("35.00"));
        assertThat(quote.weightFee()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(quote.distanceFee()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(quote.totalCost()).isEqualByComparingTo(new BigDecimal("55.00"));
        assertThat(quote.estimatedDelivery()).isEqualTo("Within 4 hours");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.05, 0.0, -1.0, 50.1, 100.0})
    @DisplayName("Weight boundary validation: must be between 0.1 kg and 50.0 kg")
    void testInvalidWeights(double invalidWeight) {
        assertThatThrownBy(() -> coordinator.calculateRate(ShippingTier.STANDARD, invalidWeight, 50.0))
                .isInstanceOf(InvalidParcelException.class)
                .hasMessageContaining("Weight must be between 0.1 kg and 50.0 kg");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.5, 0.0, -10.0, 2500.1, 5000.0})
    @DisplayName("Distance boundary validation: must be between 1.0 km and 2500.0 km")
    void testInvalidDistances(double invalidDistance) {
        assertThatThrownBy(() -> coordinator.calculateRate(ShippingTier.STANDARD, 5.0, invalidDistance))
                .isInstanceOf(InvalidParcelException.class)
                .hasMessageContaining("Distance must be between 1 km and 2,500 km");
    }
}
