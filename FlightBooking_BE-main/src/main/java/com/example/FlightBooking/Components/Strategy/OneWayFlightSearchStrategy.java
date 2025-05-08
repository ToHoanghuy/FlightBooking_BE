package com.example.FlightBooking.Components.Strategy;

import com.example.FlightBooking.Models.Flights;
import com.example.FlightBooking.Repositories.FlightRepository;

import java.sql.Timestamp;
import java.util.List;

public class OneWayFlightSearchStrategy implements FlightSearchStrategy{
    private final FlightRepository flightRepository;

    public OneWayFlightSearchStrategy(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public List<Flights> searchFlights(Long departureAirportId, Long arrivalAirportId, Timestamp departureDate, Timestamp returnDate) {
        return flightRepository.searchFlightOneWay(departureAirportId, arrivalAirportId, departureDate);
    }
}
