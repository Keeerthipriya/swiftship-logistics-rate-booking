package com.swiftship.logistics.repository;

import com.swiftship.logistics.domain.ParcelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for ParcelBooking entity.
 * Provides the persistence abstraction (DIP).
 */
@Repository
public interface BookingRepository extends JpaRepository<ParcelBooking, Long> {

    Optional<ParcelBooking> findByTrackingNumber(String trackingNumber);
}
