package com.swiftship.logistics.api.dto;

import com.swiftship.logistics.domain.BookingStatus;
import com.swiftship.logistics.domain.ParcelBooking;
import com.swiftship.logistics.domain.PaymentMethod;
import com.swiftship.logistics.domain.ShippingTier;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response payload representing a confirmed parcel booking.
 */
public record BookingResponseDto(
    Long bookingId,
    String trackingNumber,
    BookingStatus status,
    String senderName,
    String senderAddress,
    String recipientName,
    String recipientAddress,
    ShippingTier shippingTier,
    double weightKg,
    double distanceKm,
    BigDecimal totalAmount,
    String currency,
    PaymentMethod paymentMethod,
    String paymentTransactionId,
    String estimatedDelivery,
    Instant createdAt
) {
    public static BookingResponseDto fromEntity(ParcelBooking booking) {
        return new BookingResponseDto(
            booking.getId(),
            booking.getTrackingNumber(),
            booking.getStatus(),
            booking.getSenderName(),
            booking.getSenderAddress(),
            booking.getRecipientName(),
            booking.getRecipientAddress(),
            booking.getShippingTier(),
            booking.getWeightKg(),
            booking.getDistanceKm(),
            booking.getTotalAmount(),
            booking.getCurrency(),
            booking.getPaymentMethod(),
            booking.getPaymentTransactionId(),
            booking.getEstimatedDelivery(),
            booking.getCreatedAt()
        );
    }
}
