package com.swiftship.logistics.rate;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * STANDARD Rate Calculator:
 * - Base fee: $5.00
 * - Weight fee: $1.20 per kg
 * - Distance fee: $0.05 per km
 * - Delivery estimate: 3–5 business days
 */
@Component
public class StandardRateCalculator implements RateCalculator {

    private static final BigDecimal BASE_FEE = new BigDecimal("5.00");
    private static final BigDecimal RATE_PER_KG = new BigDecimal("1.20");
    private static final BigDecimal RATE_PER_KM = new BigDecimal("0.05");

    @Override
    public ShippingTier getTier() {
        return ShippingTier.STANDARD;
    }

    @Override
    public TierEligibility checkEligibility(double weightKg, double distanceKm) {
        return TierEligibility.eligible();
    }

    @Override
    public ParcelQuote calculate(double weightKg, double distanceKm) {
        BigDecimal weightFee = RATE_PER_KG.multiply(BigDecimal.valueOf(weightKg))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal distanceFee = RATE_PER_KM.multiply(BigDecimal.valueOf(distanceKm))
                .setScale(2, RoundingMode.HALF_UP);

        return ParcelQuote.of(
                ShippingTier.STANDARD,
                weightKg,
                distanceKm,
                BASE_FEE,
                weightFee,
                distanceFee,
                ShippingTier.STANDARD.getEstimatedDelivery()
        );
    }
}
