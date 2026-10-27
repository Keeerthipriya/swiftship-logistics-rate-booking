package com.swiftship.logistics.rate;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;

/**
 * Strategy contract for calculating shipping rates for a specific speed tier.
 * 
 * SOLID Principles:
 * - OCP (Open/Closed Principle): New speed tiers implement this interface and register
 *   as Spring beans without modifying the central coordinator or existing calculators.
 * - LSP (Liskov Substitution Principle): All implementations must respect shared
 *   preconditions and return contract-compliant quotes or predictable business rejections.
 */
public interface RateCalculator {

    /**
     * Returns the shipping tier handled by this calculator.
     */
    ShippingTier getTier();

    /**
     * Determines whether this calculator supports the given tier.
     */
    default boolean supports(ShippingTier tier) {
        return getTier() == tier;
    }

    /**
     * Checks whether the given parcel dimensions and distance are eligible for this tier.
     */
    TierEligibility checkEligibility(double weightKg, double distanceKm);

    /**
     * Computes the shipping quote for this tier.
     * 
     * @param weightKg Parcel weight (0.1 to 50.0 kg)
     * @param distanceKm Delivery distance (1 to 2,500 km)
     * @return ParcelQuote with fee breakdown
     * @throws com.swiftship.logistics.domain.exception.TierNotEligibleException if tier preconditions are violated
     */
    ParcelQuote calculate(double weightKg, double distanceKm);
}
