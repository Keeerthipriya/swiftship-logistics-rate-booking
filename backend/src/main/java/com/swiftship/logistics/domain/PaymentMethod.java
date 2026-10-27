package com.swiftship.logistics.domain;

/**
 * Supported payment methods for parcel bookings.
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    CORPORATE_ACCOUNT("Corporate Account Billing");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
