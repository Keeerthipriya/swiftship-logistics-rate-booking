package com.swiftship.logistics.rate;

import com.swiftship.logistics.api.dto.TierDetailDto;
import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.domain.ShippingTier;
import com.swiftship.logistics.domain.exception.InvalidParcelException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coordinates rate calculation across all registered RateCalculator strategies.
 * 
 * SOLID Principles:
 * - SRP: Exclusively responsible for coordinating rate calculator strategies and physical boundary validation.
 * - OCP: Open for extension via new RateCalculator beans; closed for modification.
 *   The coordinator discovers calculators automatically through constructor injection.
 * - DIP: Injects abstractions (RateCalculator), avoiding hardcoded references to specific tier calculators.
 */
@Service
public class RateCalculationCoordinator {

    public static final double MIN_WEIGHT_KG = 0.1;
    public static final double MAX_WEIGHT_KG = 50.0;
    public static final double MIN_DISTANCE_KM = 1.0;
    public static final double MAX_DISTANCE_KM = 2500.0;

    private final Map<ShippingTier, RateCalculator> calculators = new ConcurrentHashMap<>();

    /**
     * Constructor injection of all RateCalculator strategies (DIP).
     */
    public RateCalculationCoordinator(List<RateCalculator> rateCalculators) {
        if (rateCalculators != null) {
            for (RateCalculator calculator : rateCalculators) {
                registerCalculator(calculator);
            }
        }
    }

    /**
     * Registers a calculator strategy (supports programmatic extension during tests).
     */
    public void registerCalculator(RateCalculator calculator) {
        this.calculators.put(calculator.getTier(), calculator);
    }

    /**
     * Calculates shipping rate quote for given tier and dimensions.
     */
    public ParcelQuote calculateRate(ShippingTier tier, double weightKg, double distanceKm) {
        validatePhysicalLimits(weightKg, distanceKm);

        RateCalculator calculator = calculators.get(tier);
        if (calculator == null) {
            throw new IllegalArgumentException("No rate calculator registered for tier: " + tier);
        }

        return calculator.calculate(weightKg, distanceKm);
    }

    /**
     * Lists all registered tiers with their current eligibility based on optional parameters.
     */
    public List<TierDetailDto> getAvailableTierDetails(Double weightKg, Double distanceKm) {
        List<TierDetailDto> details = new ArrayList<>();

        for (ShippingTier tier : ShippingTier.values()) {
            RateCalculator calculator = calculators.get(tier);
            if (calculator == null) {
                continue;
            }

            boolean eligible = true;
            String ineligibilityReason = null;

            if (distanceKm != null && weightKg != null) {
                TierEligibility eligibility = calculator.checkEligibility(weightKg, distanceKm);
                eligible = eligibility.isEligible();
                ineligibilityReason = eligibility.reason();
            } else if (distanceKm != null && tier.getMaxDistanceKm() != null && distanceKm > tier.getMaxDistanceKm()) {
                eligible = false;
                ineligibilityReason = String.format("%s restricted to <= %.0f km",
                        tier.getDisplayName(), tier.getMaxDistanceKm());
            }

            details.add(TierDetailDto.from(tier, eligible, ineligibilityReason));
        }

        return details;
    }

    /**
     * Validates universal physical constraints across all tiers.
     */
    public void validatePhysicalLimits(double weightKg, double distanceKm) {
        if (weightKg < MIN_WEIGHT_KG || weightKg > MAX_WEIGHT_KG) {
            throw new InvalidParcelException(String.format(
                    "Weight must be between %.1f kg and %.1f kg (received: %.2f kg)",
                    MIN_WEIGHT_KG, MAX_WEIGHT_KG, weightKg));
        }
        if (distanceKm < MIN_DISTANCE_KM || distanceKm > MAX_DISTANCE_KM) {
            throw new InvalidParcelException(String.format(
                    "Distance must be between %.0f km and %.0f km (received: %.2f km)",
                    MIN_DISTANCE_KM, MAX_DISTANCE_KM, distanceKm));
        }
    }
}
