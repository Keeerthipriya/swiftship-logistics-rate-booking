package com.swiftship.logistics.service;

import com.swiftship.logistics.api.dto.QuoteRequestDto;
import com.swiftship.logistics.api.dto.QuoteResponseDto;
import com.swiftship.logistics.api.dto.TierDetailDto;

import java.util.List;

/**
 * Segregated Interface (ISP) for read-only rate quotes and tier inquiries.
 * Clients that only require pricing estimates depend solely on this contract.
 */
public interface QuoteService {

    /**
     * Calculates shipping quote for parcel dimensions and selected tier.
     */
    QuoteResponseDto calculateQuote(QuoteRequestDto request);

    /**
     * Retrieves all available tiers and evaluates eligibility against optional parameters.
     */
    List<TierDetailDto> getAvailableTiers(Double weightKg, Double distanceKm);
}
