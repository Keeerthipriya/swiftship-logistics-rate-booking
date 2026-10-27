package com.swiftship.logistics.service.impl;

import com.swiftship.logistics.api.dto.BookingRequestDto;
import com.swiftship.logistics.api.dto.BookingResponseDto;
import com.swiftship.logistics.api.dto.QuoteRequestDto;
import com.swiftship.logistics.api.dto.QuoteResponseDto;
import com.swiftship.logistics.domain.BookingStatus;
import com.swiftship.logistics.domain.ParcelBooking;
import com.swiftship.logistics.domain.exception.ResourceNotFoundException;
import com.swiftship.logistics.payment.PaymentCoordinator;
import com.swiftship.logistics.payment.PaymentResult;
import com.swiftship.logistics.repository.BookingRepository;
import com.swiftship.logistics.service.BookingService;
import com.swiftship.logistics.service.QuoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Implementation of BookingService.
 * 
 * SOLID Principles:
 * - SRP: Exclusively orchestrates the parcel booking workflow (quote lookup -> payment -> persistence).
 * - DIP: Depends on abstractions (QuoteService, PaymentCoordinator, BookingRepository)
 *   injected via constructor injection. No concrete infrastructure is instantiated directly.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private final QuoteService quoteService;
    private final PaymentCoordinator paymentCoordinator;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(QuoteService quoteService,
                              PaymentCoordinator paymentCoordinator,
                              BookingRepository bookingRepository) {
        this.quoteService = quoteService;
        this.paymentCoordinator = paymentCoordinator;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("BookingRequestDto must not be null");
        }

        // 1. Calculate rate and check physical & tier preconditions (e.g. SameDay <= 100km LSP check)
        QuoteRequestDto quoteRequest = new QuoteRequestDto(
                request.weightKg(),
                request.distanceKm(),
                request.shippingTier()
        );
        QuoteResponseDto quote = quoteService.calculateQuote(quoteRequest);

        // 2. Authorize simulated payment
        PaymentResult paymentResult = paymentCoordinator.executePayment(
                request.paymentMethod(),
                quote.totalCost(),
                quote.currency(),
                request.paymentReference()
        );

        // 3. Generate unique tracking number
        String trackingNumber = generateTrackingNumber();

        // 4. Construct domain entity
        ParcelBooking booking = new ParcelBooking(
                trackingNumber,
                BookingStatus.CONFIRMED,
                request.senderName(),
                request.senderAddress(),
                request.recipientName(),
                request.recipientAddress(),
                request.shippingTier(),
                request.weightKg(),
                request.distanceKm(),
                quote.baseFee(),
                quote.weightFee(),
                quote.distanceFee(),
                quote.totalCost(),
                quote.currency(),
                request.paymentMethod(),
                paymentResult.transactionId(),
                quote.estimatedDelivery()
        );

        // 5. Persist to H2 database via repository abstraction (DIP)
        ParcelBooking savedBooking = bookingRepository.save(booking);

        return BookingResponseDto.fromEntity(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long id) {
        ParcelBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        return BookingResponseDto.fromEntity(booking);
    }

    private String generateTrackingNumber() {
        int year = LocalDate.now().getYear();
        String uuidSegment = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("SWIFT-%d-%s", year, uuidSegment);
    }
}
