package com.example.FlightBooking.Components.Strategy;

import com.example.FlightBooking.Models.Flights;

import java.sql.Timestamp;
import java.util.List;

public interface FlightSearchStrategy {
    List<Flights> searchFlights(Long departureAirportId, Long arrivalAirportId, Timestamp departureDate, Timestamp returnDate);
}
