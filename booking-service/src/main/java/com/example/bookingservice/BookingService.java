package com.example.bookingservice;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelClient hotelClient;

    public List<Booking> getAllBookings(){
        return bookingRepository.findAll();

    }
    public Booking getBookingById(Long id){
        return bookingRepository.findById(id).orElseThrow(()->new RuntimeException("Booking not found"));
    }
    @Transactional
    public Booking createBooking(Booking booking) {

        HotelDto hotel;
        try {
            hotel = hotelClient.getHotelById(booking.getHotelId());
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Hotel with id " + booking.getHotelId() + " not found"
            );
        }

        long availableCount = hotel.getRooms().stream()
                .filter(r -> Boolean.TRUE.equals(r.getAvailable()))
                .count();
        if (availableCount == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No available rooms at " + hotel.getName());
        }

        boolean taken = bookingRepository
                .existsByRoomIdAndCheckOutAfterAndCheckInBefore(
                        booking.getRoomId(),
                        booking.getCheckIn(),
                        booking.getCheckOut()
                );

        if (taken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Room " + booking.getRoomId() +
                            " is already booked for the selected dates");
        }

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long id, Booking updated) {
        Booking existing = getBookingById(id);
        existing.setHotelId(updated.getHotelId());
        existing.setRoomId(updated.getRoomId());
        existing.setGuestName(updated.getGuestName());
        existing.setCheckIn(updated.getCheckIn());
        existing.setCheckOut(updated.getCheckOut());
        existing.setStatus(updated.getStatus());
        return bookingRepository.save(existing);
    }

    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    public List<Booking> findByDateRange(LocalDate start, LocalDate end) {
        return bookingRepository.findByCheckInBetween(start, end);
    }

    public List<Booking> findByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);

    }

    public List<Booking> findByGuestName(String guestName) {
        return bookingRepository.findByGuestName(guestName);
    }

    public List<Booking> findByHotelId(Long hotelId) {
        return bookingRepository.findByHotelId(hotelId);


    }

    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        booking.setStatus(Booking.Status.CANCELLED);
        return bookingRepository.save(booking);
    }



    public boolean isRoomAvailable(Long roomId,
                                   LocalDate checkIn,
                                   LocalDate checkOut) {

        boolean overlaps = bookingRepository
                .existsByRoomIdAndCheckOutAfterAndCheckInBefore(
                        roomId,
                        checkIn,
                        checkOut
                );

        // If there is an overlapping booking, room is NOT available
        return !overlaps;
    }


    public  long getTotalBookings() {
        return bookingRepository.count();
    }

    public long getBookingsByHotelId(Long hotelId) {
        return bookingRepository.countByHotelId(hotelId);
    }

    public long getBookingsByRoomId(Long roomId) {
        return bookingRepository.countByRoomId(roomId);
    }

}
