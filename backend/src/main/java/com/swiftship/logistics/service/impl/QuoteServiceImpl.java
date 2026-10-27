package com.swiftship.logistics.service.impl;

import com.swiftship.logistics.api.dto.QuoteRequestDto;
import com.swiftship.logistics.api.dto.QuoteResponseDto;
import com.swiftship.logistics.api.dto.TierDetailDto;
import com.swiftship.logistics.domain.ParcelQuote;
import com.swiftship.logistics.rate.RateCalculationCoordinator;
import com.swiftship.logistics.service.QuoteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of QuoteService.
 * Focuses strictly on quote calculation and tier details (SRP).
 */
@Service
public class QuoteServiceImpl implements QuoteService {

    private final RateCalculationCoordinator rateCoordinator;

    public QuoteServiceImpl(RateCalculationCoordinator rateCoordinator) {
        this.rateCoordinator = rateCoordinator;
    }

    @Override
    public QuoteResponseDto calculateQuote(QuoteRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("QuoteRequestDto must not be null");
        }

        ParcelQuote quote = rateCoordinator.calculateRate(
                request.shippingTier(),
                request.weightKg(),
                request.distanceKm()
        );

        return QuoteResponseDto.fromDomain(quote);
    }

    @Override
    public List<TierDetailDto> getAvailableTiers(Double weightKg, Double distanceKm) {
        return rateCoordinator.getAvailableTierDetails(weightKg, distanceKm);
    }
}
