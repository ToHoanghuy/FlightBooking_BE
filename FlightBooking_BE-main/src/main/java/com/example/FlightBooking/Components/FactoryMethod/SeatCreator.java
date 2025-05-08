package com.example.FlightBooking.Components.FactoryMethod;

import com.example.FlightBooking.Models.Flights;

import java.util.Map;

public abstract class SeatCreator {
    public abstract SeatFactory createSeatFactory();

    public Map<String, Map<String, String>> generateSeats(Flights flights) {
        SeatFactory factory = createSeatFactory();
        return factory.createSeats(flights);
    }
}
