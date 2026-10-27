package com.swiftship.logistics.rate;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;
import com.swiftship.logistics.domain.exception.TierNotEligibleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SAME_DAY Rate Calculator:
 * - Base fee: $35.00
 * - Weight fee: $4.00 per kg
 * - Distance fee: $0.25 per km
 * - Delivery estimate: Within 4 hours
 * 
 * Liskov Substitution Principle (LSP) Enforcement:
 * Same-Day delivery is strictly restricted to distances <= 100 km.
 * When requested for distance > 100 km, calculate() does NOT throw a generic
 * unhandled runtime error or corrupt calculations. Instead, it rejects with
 * a predictable, documented business exception (TierNotEligibleException).
 */
@Component
public class SameDayRateCalculator implements RateCalculator {

    public static final double MAX_DISTANCE_KM = 100.0;
    private static final BigDecimal BASE_FEE = new BigDecimal("35.00");
    private static final BigDecimal RATE_PER_KG = new BigDecimal("4.00");
    private static final BigDecimal RATE_PER_KM = new BigDecimal("0.25");

    @Override
    public ShippingTier getTier() {
        return ShippingTier.SAME_DAY;
    }

    @Override
    public TierEligibility checkEligibility(double weightKg, double distanceKm) {
        if (distanceKm > MAX_DISTANCE_KM) {
            return TierEligibility.ineligible(
                String.format("Same-Day delivery is restricted to distances <= 100 km (requested: %.1f km)", distanceKm)
            );
        }
        return TierEligibility.eligible();
    }

    @Override
    public ParcelQuote calculate(double weightKg, double distanceKm) {
        // Enforce LSP precondition
        if (distanceKm > MAX_DISTANCE_KM) {
            throw TierNotEligibleException.sameDayExceedsMaxDistance(distanceKm);
        }

        BigDecimal weightFee = RATE_PER_KG.multiply(BigDecimal.valueOf(weightKg))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal distanceFee = RATE_PER_KM.multiply(BigDecimal.valueOf(distanceKm))
                .setScale(2, RoundingMode.HALF_UP);

        return ParcelQuote.of(
                ShippingTier.SAME_DAY,
                weightKg,
                distanceKm,
                BASE_FEE,
                weightFee,
                distanceFee,
                ShippingTier.SAME_DAY.getEstimatedDelivery()
        );
    }
}
