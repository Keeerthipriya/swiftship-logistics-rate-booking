package com.swiftship.logistics.domain;

import java.math.BigDecimal;

/**
 * Shipping speed tiers offered by SwiftShip Logistics.
 * 
 * STANDARD: 3–5 business days, Base fee $5.00 + $1.20 per kg + $0.05 per km.
 * EXPRESS: Next day, Base fee $15.00 + $2.50 per kg + $0.12 per km.
 * SAME_DAY: Within 4 hours, Base fee $35.00 + $4.00 per kg + $0.25 per km (max 100 km).
 */
public enum ShippingTier {
    STANDARD(
        "Standard Delivery",
        "Cost-effective shipping arriving in 3–5 business days.",
        new BigDecimal("5.00"),
        new BigDecimal("1.20"),
        new BigDecimal("0.05"),
        "3–5 business days",
        null
    ),
    EXPRESS(
        "Express Delivery",
        "Fast guaranteed delivery next business day.",
        new BigDecimal("15.00"),
        new BigDecimal("2.50"),
        new BigDecimal("0.12"),
        "Next day",
        null
    ),
    SAME_DAY(
        "Same-Day Delivery",
        "Direct courier rush delivery within 4 hours (restricted to <= 100 km).",
        new BigDecimal("35.00"),
        new BigDecimal("4.00"),
        new BigDecimal("0.25"),
        "Within 4 hours",
        100.0
    );

    private final String displayName;
    private final String description;
    private final BigDecimal baseFee;
    private final BigDecimal perKgRate;
    private final BigDecimal perKmRate;
    private final String estimatedDelivery;
    private final Double maxDistanceKm;

    ShippingTier(String displayName,
                 String description,
                 BigDecimal baseFee,
                 BigDecimal perKgRate,
                 BigDecimal perKmRate,
                 String estimatedDelivery,
                 Double maxDistanceKm) {
        this.displayName = displayName;
        this.description = description;
        this.baseFee = baseFee;
        this.perKgRate = perKgRate;
        this.perKmRate = perKmRate;
        this.estimatedDelivery = estimatedDelivery;
        this.maxDistanceKm = maxDistanceKm;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public BigDecimal getPerKgRate() {
        return perKgRate;
    }

    public BigDecimal getPerKmRate() {
        return perKmRate;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public Double getMaxDistanceKm() {
        return maxDistanceKm;
    }
}
