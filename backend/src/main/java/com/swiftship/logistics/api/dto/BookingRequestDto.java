package com.swiftship.logistics.api.dto;

import com.swiftship.logistics.domain.PaymentMethod;
import com.swiftship.logistics.domain.ShippingTier;
import jakarta.validation.constraints.*;

/**
 * Request payload for creating a parcel booking.
 */
public record BookingRequestDto(
    @NotBlank(message = "senderName is required")
    @Size(min = 2, max = 100, message = "senderName must be between 2 and 100 characters")
    String senderName,

    @NotBlank(message = "senderAddress is required")
    @Size(min = 5, max = 255, message = "senderAddress must be between 5 and 255 characters")
    String senderAddress,

    @NotBlank(message = "recipientName is required")
    @Size(min = 2, max = 100, message = "recipientName must be between 2 and 100 characters")
    String recipientName,

    @NotBlank(message = "recipientAddress is required")
    @Size(min = 5, max = 255, message = "recipientAddress must be between 5 and 255 characters")
    String recipientAddress,

    @NotNull(message = "weightKg is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    @DecimalMax(value = "50.0", message = "Weight must not exceed 50.0 kg")
    Double weightKg,

    @NotNull(message = "distanceKm is required")
    @DecimalMin(value = "1.0", message = "Distance must be at least 1 km")
    @DecimalMax(value = "2500.0", message = "Distance must not exceed 2,500 km")
    Double distanceKm,

    @NotNull(message = "shippingTier is required")
    ShippingTier shippingTier,

    @NotNull(message = "paymentMethod is required")
    PaymentMethod paymentMethod,

    String paymentReference
) {
}
