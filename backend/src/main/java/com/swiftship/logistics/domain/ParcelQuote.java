package com.swiftship.logistics.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable Domain Value Object representing a computed shipping rate quote.
 */
public record ParcelQuote(
    UUID quoteId,
    ShippingTier tier,
    double weightKg,
    double distanceKm,
    BigDecimal baseFee,
    BigDecimal weightFee,
    BigDecimal distanceFee,
    BigDecimal totalCost,
    String currency,
    String estimatedDelivery,
    Instant createdAt
) {
    public static ParcelQuote of(ShippingTier tier,
                                 double weightKg,
                                 double distanceKm,
                                 BigDecimal baseFee,
                                 BigDecimal weightFee,
                                 BigDecimal distanceFee,
                                 String estimatedDelivery) {
        BigDecimal total = baseFee.add(weightFee).add(distanceFee);
        return new ParcelQuote(
            UUID.randomUUID(),
            tier,
            weightKg,
            distanceKm,
            baseFee,
            weightFee,
            distanceFee,
            total,
            "USD",
            estimatedDelivery,
            Instant.now()
        );
    }
}
