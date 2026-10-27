package com.swiftship.logistics.domain.exception;

import com.swiftship.logistics.domain.ShippingTier;

/**
 * Thrown when a requested shipping tier cannot be fulfilled for the given parcel parameters.
 * Demonstrates the Liskov Substitution Principle (LSP):
 * Subtypes and implementations must preserve documented preconditions and signal violations
 * through explicit, predictable business rejections rather than unhandled crashes.
 *
 * Example: Same-Day delivery requested for distance > 100 km.
 */
public class TierNotEligibleException extends RuntimeException {

    private final ShippingTier tier;
    private final double distanceKm;
    private final Double maxAllowedKm;

    public TierNotEligibleException(ShippingTier tier, double distanceKm, Double maxAllowedKm, String reason) {
        super(reason);
        this.tier = tier;
        this.distanceKm = distanceKm;
        this.maxAllowedKm = maxAllowedKm;
    }

    public static TierNotEligibleException sameDayExceedsMaxDistance(double distanceKm) {
        return new TierNotEligibleException(
            ShippingTier.SAME_DAY,
            distanceKm,
            100.0,
            String.format("Same-Day delivery is restricted to distances <= 100 km (requested: %.1f km)", distanceKm)
        );
    }

    public ShippingTier getTier() {
        return tier;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public Double getMaxAllowedKm() {
        return maxAllowedKm;
    }
}
