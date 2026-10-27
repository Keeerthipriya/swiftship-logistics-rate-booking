package com.swiftship.logistics.api.dto;

import com.swiftship.logistics.domain.ShippingTier;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for quote calculation.
 */
public record QuoteRequestDto(
    @NotNull(message = "weightKg is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    @DecimalMax(value = "50.0", message = "Weight must not exceed 50.0 kg")
    Double weightKg,

    @NotNull(message = "distanceKm is required")
    @DecimalMin(value = "1.0", message = "Distance must be at least 1 km")
    @DecimalMax(value = "2500.0", message = "Distance must not exceed 2,500 km")
    Double distanceKm,

    @NotNull(message = "shippingTier is required")
    ShippingTier shippingTier
) {
}
