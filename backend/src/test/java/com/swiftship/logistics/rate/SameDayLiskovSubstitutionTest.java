package com.swiftship.logistics.rate;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;
import com.swiftship.logistics.domain.exception.TierNotEligibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Demonstrates the Liskov Substitution Principle (LSP):
 * All RateCalculator implementations honor the contract.
 * SameDayRateCalculator strictly enforces its documented precondition (<= 100 km).
 * When distance > 100 km, it signals a predictable business rejection (TierNotEligibleException)
 * rather than failing with an unhandled runtime exception or giving corrupt numbers.
 */
class SameDayLiskovSubstitutionTest {

    private RateCalculationCoordinator coordinator;
    private SameDayRateCalculator sameDayCalculator;

    @BeforeEach
    void setUp() {
        sameDayCalculator = new SameDayRateCalculator();
        coordinator = new RateCalculationCoordinator(List.of(sameDayCalculator));
    }

    @Test
    @DisplayName("LSP Success: Same-Day at 100.0 km boundary succeeds")
    void testSameDayAtExactMaxBoundary() {
        ParcelQuote quote = coordinator.calculateRate(ShippingTier.SAME_DAY, 5.0, 100.0);
        assertThat(quote).isNotNull();
        assertThat(quote.tier()).isEqualTo(ShippingTier.SAME_DAY);
    }

    @ParameterizedTest
    @ValueSource(doubles = {100.1, 105.0, 250.0, 1500.0})
    @DisplayName("LSP Violation Rejection: Same-Day > 100 km predictably throws TierNotEligibleException")
    void testSameDayRejectionOver100Km(double excessiveDistance) {
        assertThatThrownBy(() -> coordinator.calculateRate(ShippingTier.SAME_DAY, 5.0, excessiveDistance))
                .isInstanceOf(TierNotEligibleException.class)
                .hasMessageContaining("restricted to distances <= 100 km")
                .satisfies(ex -> {
                    TierNotEligibleException tierEx = (TierNotEligibleException) ex;
                    assertThat(tierEx.getTier()).isEqualTo(ShippingTier.SAME_DAY);
                    assertThat(tierEx.getDistanceKm()).isEqualTo(excessiveDistance);
                    assertThat(tierEx.getMaxAllowedKm()).isEqualTo(100.0);
                });
    }

    @Test
    @DisplayName("LSP Eligibility Check: checkEligibility returns false with clear reason")
    void testSameDayEligibilityEvaluation() {
        TierEligibility eligible = sameDayCalculator.checkEligibility(5.0, 80.0);
        assertThat(eligible.isEligible()).isTrue();
        assertThat(eligible.reason()).isNull();

        TierEligibility ineligible = sameDayCalculator.checkEligibility(5.0, 120.0);
        assertThat(ineligible.isEligible()).isFalse();
        assertThat(ineligible.reason()).contains("restricted to distances <= 100 km");
    }
}
