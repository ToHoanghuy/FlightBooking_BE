package com.example.FlightBooking.Components.FactoryMethod;

import com.example.FlightBooking.Models.Flights;
import com.example.FlightBooking.Models.Planes;

import java.util.Map;

public interface SeatFactory {
    Map<String, Map<String, String>> createSeats(Flights flights);
}