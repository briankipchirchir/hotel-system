package com.example.bookingservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final HotelClient hotelClient;
    @GetMapping
    public List<Booking> getAllBookings(){
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Booking booking) {

        Booking createdBooking = bookingService.createBooking(booking);

        HotelDto hotel = hotelClient.getHotelById(createdBooking.getHotelId());
        HotelDto.RoomDto selectedRoom = hotel.getRooms().stream()
                .filter(r -> r.getId().equals(createdBooking.getRoomId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room " + createdBooking.getRoomId() + " not found in hotel " + hotel.getName()
                ));

        // Build a custom JSON response
        Map<String, Object> response = new HashMap<>();
        response.put(
                "message",
                "Booking successfully created for dates " +
                        createdBooking.getCheckIn() + " to " + createdBooking.getCheckOut()
        );
        response.put("booking", createdBooking);
        response.put("roomPrice", selectedRoom.getPrice());

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long id,
            @RequestBody Booking booking){

        Booking updatedBooking = bookingService.updateBooking(id, booking);
        return ResponseEntity.ok(updatedBooking);

    }

    @DeleteMapping("/{id}")

    public ResponseEntity<String> deleteBooking(@PathVariable Long id){
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking successfully deleted");
    }

    @GetMapping("/range")
    public List<Booking> getBookingsByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return bookingService.findByDateRange(start, end);
    }


    @GetMapping("/hotel/{hotelId}")

    public List<Booking> getBookingsByHotel(@PathVariable Long hotelId) {

       return  bookingService.findByHotelId(hotelId);

    }

    @GetMapping("/room/{roomId}")

    public List<Booking> getBookingsByRoom(@PathVariable Long roomId) {
        return  bookingService.findByRoomId(roomId);
    }

    @GetMapping("/guest")
    public List<Booking> getBookingsByGuest(@RequestParam String name) {
        return bookingService.findByGuestName(name);
    }

   @PatchMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        Booking cancelled = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelled);
   }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> checkRoomAvailability(
            @RequestParam Long roomId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        boolean available = bookingService.isRoomAvailable(roomId, start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("roomId", roomId);
        response.put("available", available);
        response.put("start", start);
        response.put("end", end);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalBookings() {
        long count = bookingService.getTotalBookings();
        return ResponseEntity.ok(Map.of("totalBookings", count));
    }


    // Bookings for a specific hotel
    @GetMapping("/count/hotel/{hotelId}")
    public ResponseEntity<Map<String, Long>> getBookingsCountByHotel(@PathVariable Long hotelId) {
        long count = bookingService.getBookingsByHotelId(hotelId);
        return ResponseEntity.ok(Map.of("hotelId", hotelId, "bookings", count));
    }

    // Bookings for a specific room
    @GetMapping("/count/room/{roomId}")
    public ResponseEntity<Map<String, Long>> getBookingsCountByRoom(@PathVariable Long roomId) {
        long count = bookingService.getBookingsByRoomId(roomId);
        return ResponseEntity.ok(Map.of("roomId", roomId, "bookings", count));
    }






}
