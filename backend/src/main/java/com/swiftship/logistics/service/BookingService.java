package com.swiftship.logistics.service;

import com.swiftship.logistics.api.dto.BookingRequestDto;
import com.swiftship.logistics.api.dto.BookingResponseDto;

/**
 * Segregated Interface (ISP) for booking creation and retrieval mutations.
 */
public interface BookingService {

    /**
     * Orchestrates rate calculation, payment processing, and parcel persistence.
     */
    BookingResponseDto createBooking(BookingRequestDto request);

    /**
     * Retrieves an existing booking by its primary ID.
     */
    BookingResponseDto getBookingById(Long id);
}
