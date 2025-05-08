package com.example.FlightBooking.Components.Strategy;

import com.example.FlightBooking.Models.Flights;
import com.example.FlightBooking.Repositories.FlightRepository;

import java.sql.Timestamp;
import java.util.List;

public class RoundTripFlightSearchStrategy implements FlightSearchStrategy{
    private final FlightRepository flightRepository;

    public RoundTripFlightSearchStrategy(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public List<Flights> searchFlights(Long departureAirportId, Long arrivalAirportId, Timestamp departureDate, Timestamp returnDate) {
        return flightRepository.searchFlightRoundTrip(departureAirportId, arrivalAirportId, departureDate, returnDate);
    }
}
