package com.swiftship.logistics.api;

import com.swiftship.logistics.api.dto.BookingRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * JAX-RS API contract for booking creation and lookup.
 * Pure JAX-RS Jakarta REST annotations (No Spring MVC @RestController).
 */
@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BookingApi {

    @POST
    Response createBooking(@NotNull @Valid BookingRequestDto request);

    @GET
    @Path("/{id}")
    Response getBookingById(@PathParam("id") Long id);
}
