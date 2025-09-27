package com.example.bookingservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String country;
    private List<RoomDto> rooms;

    @Data
    public static class RoomDto {
        private Long id;
        private String type;
        private BigDecimal price;
        private Boolean available;
    }
}
