package com.addroit.booking.repository;

import com.addroit.booking.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Fetch logged-in attendee's bookings with latest booking shown first
    List<Booking> findByAttendeeIdOrderByBookedAtDesc(
            Long attendeeId
    );

    // Lock booking row and verify ownership to prevent duplicate cancellation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.id = :bookingId
              AND b.attendee.id = :attendeeId
            """)
    Optional<Booking> findOwnedBookingForUpdate(
            @Param("bookingId") Long bookingId,
            @Param("attendeeId") Long attendeeId
    );
}