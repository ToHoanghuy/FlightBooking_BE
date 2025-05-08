package com.example.FlightBooking.DTOs.Request.Booking;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CombineBookingRequestDTO {
    private List<BookingRequestDTO> bookingRequests;
}
