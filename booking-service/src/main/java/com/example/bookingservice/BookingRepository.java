package com.example.bookingservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByHotelId(Long Id);
    List<Booking> findByGuestName(String guestName);
    List<Booking> findByCheckInBetween(LocalDate start, LocalDate end);

    // count by hotel
    long countByHotelId(Long hotelId);

    // count by room
    long countByRoomId(Long roomId);

    // ✅ New method for availability check:
    // Returns true if there is at least one booking whose dates overlap the requested range
    boolean existsByRoomIdAndCheckOutAfterAndCheckInBefore(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    );

    List<Booking> findByRoomId(Long roomId);

}
