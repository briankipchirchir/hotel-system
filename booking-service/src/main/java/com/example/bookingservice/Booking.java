package com.example.bookingservice;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;   // reference to hotel
    private Long roomId;    // reference to room
    private String guestName;

    // ✅ Add the format annotation here
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;

    // ✅ And here
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;


    @Enumerated(EnumType.STRING)
    @Builder.Default                // keeps default when using @Builder
    private Status status = Status.CONFIRMED;

    public enum Status {
        CONFIRMED,
        CANCELLED
    }
}
