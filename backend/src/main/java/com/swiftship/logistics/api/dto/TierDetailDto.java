package com.swiftship.logistics.api.dto;

import com.swiftship.logistics.domain.ShippingTier;
import java.math.BigDecimal;

/**
 * Detailed information about a shipping tier and its current eligibility.
 */
public record TierDetailDto(
    ShippingTier tier,
    String displayName,
    String description,
    BigDecimal baseFee,
    BigDecimal perKgRate,
    BigDecimal perKmRate,
    String estimatedDelivery,
    Double maxDistanceKm,
    boolean eligible,
    String ineligibilityReason
) {
    public static TierDetailDto from(ShippingTier tier, boolean eligible, String ineligibilityReason) {
        return new TierDetailDto(
            tier,
            tier.getDisplayName(),
            tier.getDescription(),
            tier.getBaseFee(),
            tier.getPerKgRate(),
            tier.getPerKmRate(),
            tier.getEstimatedDelivery(),
            tier.getMaxDistanceKm(),
            eligible,
            ineligibilityReason
        );
    }
}
