package com.swiftship.logistics.api.impl;

import com.swiftship.logistics.api.QuoteApi;
import com.swiftship.logistics.api.dto.QuoteRequestDto;
import com.swiftship.logistics.api.dto.QuoteResponseDto;
import com.swiftship.logistics.api.dto.TierDetailDto;
import com.swiftship.logistics.service.QuoteService;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Apache CXF JAX-RS Resource implementation for Quotes and Tiers.
 * 
 * SOLID Principles:
 * - SRP: Exclusively focused on HTTP transport, parameter binding, and JAX-RS Response building.
 * - DIP: Injects QuoteService abstraction via constructor.
 */
@Component
public class QuoteResourceImpl implements QuoteApi {

    private final QuoteService quoteService;

    public QuoteResourceImpl(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @Override
    public Response calculateQuote(QuoteRequestDto request) {
        QuoteResponseDto response = quoteService.calculateQuote(request);
        return Response.ok(response).build();
    }

    @Override
    public Response getShippingTiers(Double weightKg, Double distanceKm) {
        List<TierDetailDto> tiers = quoteService.getAvailableTiers(weightKg, distanceKm);
        return Response.ok(tiers).build();
    }
}
