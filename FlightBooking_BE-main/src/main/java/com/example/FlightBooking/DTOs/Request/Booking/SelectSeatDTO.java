package com.example.FlightBooking.DTOs.Request.Booking;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SelectSeatDTO {
    private Long flightId;
    private List<String> selectedSeats;
}
