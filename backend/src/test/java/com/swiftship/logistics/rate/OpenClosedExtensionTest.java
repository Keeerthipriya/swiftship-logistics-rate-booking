package com.swiftship.logistics.rate;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates the Open/Closed Principle (OCP):
 * Shows that a new custom shipping tier calculation strategy can be added to the engine
 * without modifying RateCalculationCoordinator or altering existing tier implementations.
 */
class OpenClosedExtensionTest {

    @Test
    @DisplayName("OCP Demonstration: Dynamically plug in a new rate strategy without modifying coordinator")
    void testOpenClosedExtension() {
        // Base coordinators start with STANDARD and EXPRESS
        List<RateCalculator> existingCalculators = new ArrayList<>(List.of(
                new StandardRateCalculator(),
                new ExpressRateCalculator()
        ));
        RateCalculationCoordinator coordinator = new RateCalculationCoordinator(existingCalculators);

        // Verify standard behavior before extension
        ParcelQuote standardQuote = coordinator.calculateRate(ShippingTier.STANDARD, 10.0, 50.0);
        assertThat(standardQuote.totalCost()).isEqualByComparingTo(new BigDecimal("19.50"));

        // Extension Point: A developer introduces a customized tier calculator
        // without touching existing classes
        RateCalculator customExtensionCalculator = new RateCalculator() {
            @Override
            public ShippingTier getTier() {
                return ShippingTier.SAME_DAY;
            }

            @Override
            public TierEligibility checkEligibility(double weightKg, double distanceKm) {
                return TierEligibility.eligible();
            }

            @Override
            public ParcelQuote calculate(double weightKg, double distanceKm) {
                // Custom flat rate strategy
                return ParcelQuote.of(
                        ShippingTier.SAME_DAY,
                        weightKg,
                        distanceKm,
                        new BigDecimal("20.00"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "Special Promotional Same Day"
                );
            }
        };

        // Register the new strategy into the coordinator
        coordinator.registerCalculator(customExtensionCalculator);

        // Execute using the new strategy: Coordinator uses the extension seamlessly
        ParcelQuote extendedQuote = coordinator.calculateRate(ShippingTier.SAME_DAY, 10.0, 50.0);
        assertThat(extendedQuote.totalCost()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(extendedQuote.estimatedDelivery()).isEqualTo("Special Promotional Same Day");

        // Verify existing calculators remain completely unaffected
        ParcelQuote unchangedStandardQuote = coordinator.calculateRate(ShippingTier.STANDARD, 10.0, 50.0);
        assertThat(unchangedStandardQuote.totalCost()).isEqualByComparingTo(new BigDecimal("19.50"));
    }
}
