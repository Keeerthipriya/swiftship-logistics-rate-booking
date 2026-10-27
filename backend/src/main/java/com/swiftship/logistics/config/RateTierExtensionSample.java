package com.swiftship.logistics.config;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;
import com.swiftship.logistics.rate.RateCalculator;
import com.swiftship.logistics.rate.TierEligibility;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * WRITTEN EXAMPLE: Demonstrating Open/Closed Principle (OCP) Extension Point.
 * 
 * To add a new shipping tier (e.g. OVERNIGHT or ECO_FREIGHT) to SwiftShip:
 * 1. Implement the {@link RateCalculator} interface.
 * 2. Annotate the new class with {@code @Component}.
 * 
 * The {@link com.swiftship.logistics.rate.RateCalculationCoordinator} will automatically
 * discover and wire it into the active rate calculation strategies without modifying a single
 * line of coordinator or existing tier code.
 * 
 * (This sample demonstrates the architecture; it is not registered as a Spring Bean so the
 * core business tiers remain strictly STANDARD, EXPRESS, and SAME_DAY).
 */
public class RateTierExtensionSample implements RateCalculator {

    private static final BigDecimal BASE_FEE = new BigDecimal("8.00");
    private static final BigDecimal PER_KG = new BigDecimal("0.90");
    private static final BigDecimal PER_KM = new BigDecimal("0.04");

    @Override
    public ShippingTier getTier() {
        // In a real extension, ShippingTier would be extended or replaced with a TierId value object
        return ShippingTier.STANDARD;
    }

    @Override
    public TierEligibility checkEligibility(double weightKg, double distanceKm) {
        return TierEligibility.eligible();
    }

    @Override
    public ParcelQuote calculate(double weightKg, double distanceKm) {
        BigDecimal weightFee = PER_KG.multiply(BigDecimal.valueOf(weightKg)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal distanceFee = PER_KM.multiply(BigDecimal.valueOf(distanceKm)).setScale(2, RoundingMode.HALF_UP);

        return ParcelQuote.of(
                getTier(),
                weightKg,
                distanceKm,
                BASE_FEE,
                weightFee,
                distanceFee,
                "5–7 business days (Eco)"
        );
    }
}
