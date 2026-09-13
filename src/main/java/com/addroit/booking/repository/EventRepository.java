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

    List<Event> findByOrganiserIdOrderByCreatedAtDesc(Long organiserId);

    List<Event> findByEventDateAfterOrderByEventDateAsc(
            LocalDateTime currentDateTime
    );

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