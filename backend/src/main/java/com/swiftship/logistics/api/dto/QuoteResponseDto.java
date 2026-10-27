package com.swiftship.logistics.api.dto;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Response payload representing a calculated quote.
 */
public record QuoteResponseDto(
    UUID quoteId,
    ShippingTier shippingTier,
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
    public static QuoteResponseDto fromDomain(ParcelQuote quote) {
        return new QuoteResponseDto(
            quote.quoteId(),
            quote.tier(),
            quote.weightKg(),
            quote.distanceKm(),
            quote.baseFee(),
            quote.weightFee(),
            quote.distanceFee(),
            quote.totalCost(),
            quote.currency(),
            quote.estimatedDelivery(),
            quote.createdAt()
        );
    }
}
