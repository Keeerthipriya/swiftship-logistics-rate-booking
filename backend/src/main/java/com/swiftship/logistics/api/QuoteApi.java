package com.swiftship.logistics.api;

import com.swiftship.logistics.api.dto.QuoteRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * JAX-RS API contract for quotes and tier metadata.
 * Note: Uses pure JAX-RS Jakarta REST annotations (No Spring MVC @RestController).
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface QuoteApi {

    @POST
    @Path("/quotes")
    Response calculateQuote(@NotNull @Valid QuoteRequestDto request);

    @GET
    @Path("/tiers")
    Response getShippingTiers(
        @QueryParam("weightKg") Double weightKg,
        @QueryParam("distanceKm") Double distanceKm
    );
}
