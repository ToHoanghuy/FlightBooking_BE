package com.example.FlightBooking.Components.Strategy;

import com.example.FlightBooking.Models.Flights;

import java.sql.Timestamp;
import java.util.List;

public class FlightSearchContext {
    private FlightSearchStrategy strategy;

    public void setStrategy(FlightSearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Flights> searchFlights(Long departureAirportId, Long arrivalAirportId, Timestamp departureDate, Timestamp returnDate) {
        return strategy.searchFlights(departureAirportId, arrivalAirportId, departureDate, returnDate);
    }
}
