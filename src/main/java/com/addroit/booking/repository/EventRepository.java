package com.addroit.booking.repository;

import com.addroit.booking.entity.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Fetch events created by organiser, latest created event first
    List<Event> findByOrganiserIdOrderByCreatedAtDesc(Long organiserId);

    // Fetch only upcoming events and show nearest event first
    List<Event> findByEventDateAfterOrderByEventDateAsc(
            LocalDateTime currentDateTime
    );

    // Lock event row before updating ticket availability to prevent overselling
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT e
            FROM Event e
            WHERE e.id = :eventId
            """)
    Optional<Event> findByIdForUpdate(
            @Param("eventId") Long eventId
    );
}