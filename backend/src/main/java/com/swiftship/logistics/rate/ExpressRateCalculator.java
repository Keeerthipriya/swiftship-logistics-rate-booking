package com.swiftship.logistics.rate;

import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * EXPRESS Rate Calculator:
 * - Base fee: $15.00
 * - Weight fee: $2.50 per kg
 * - Distance fee: $0.12 per km
 * - Delivery estimate: Next day
 */
@Component
public class ExpressRateCalculator implements RateCalculator {

    private static final BigDecimal BASE_FEE = new BigDecimal("15.00");
    private static final BigDecimal RATE_PER_KG = new BigDecimal("2.50");
    private static final BigDecimal RATE_PER_KM = new BigDecimal("0.12");

    @Override
    public ShippingTier getTier() {
        return ShippingTier.EXPRESS;
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
                ShippingTier.EXPRESS,
                weightKg,
                distanceKm,
                BASE_FEE,
                weightFee,
                distanceFee,
                ShippingTier.EXPRESS.getEstimatedDelivery()
        );
    }
}
