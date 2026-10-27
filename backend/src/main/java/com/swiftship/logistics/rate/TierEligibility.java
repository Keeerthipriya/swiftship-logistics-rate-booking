package com.swiftship.logistics.rate;

/**
 * Result of eligibility evaluation for a shipping tier.
 */
public record TierEligibility(boolean isEligible, String reason) {
    public static TierEligibility eligible() {
        return new TierEligibility(true, null);
    }

    public static TierEligibility ineligible(String reason) {
        return new TierEligibility(false, reason);
    }
}
