package com.swiftship.logistics.api.impl;

import com.swiftship.logistics.api.BookingApi;
import com.swiftship.logistics.api.dto.BookingRequestDto;
import com.swiftship.logistics.api.dto.BookingResponseDto;
import com.swiftship.logistics.service.BookingService;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Apache CXF JAX-RS Resource implementation for Bookings.
 * 
 * SOLID Principles:
 * - SRP: Exclusively focused on HTTP transport, status codes, and URI headers.
 * - DIP: Injects BookingService abstraction via constructor.
 */
@Component
public class BookingResourceImpl implements BookingApi {

    private final BookingService bookingService;

    public BookingResourceImpl(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public Response createBooking(BookingRequestDto request) {
        BookingResponseDto response = bookingService.createBooking(request);
        URI location = URI.create("/api/bookings/" + response.bookingId());
        return Response.created(location).entity(response).build();
    }

    @Override
    public Response getBookingById(Long id) {
        BookingResponseDto response = bookingService.getBookingById(id);
        return Response.ok(response).build();
    }
}
