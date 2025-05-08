package com.example.FlightBooking.Components.FactoryMethod;

import com.example.FlightBooking.Enum.SeatClass;
import com.example.FlightBooking.Enum.SeatStatus;
import com.example.FlightBooking.Models.Flights;
import com.example.FlightBooking.Models.Planes;

import java.util.HashMap;
import java.util.Map;

public class FirstClassSeatFactory implements SeatFactory{
    private static final int NUM_ROWS = 6;
    private static final int SEATS_PER_ROW = 4;
    private static final String[] SEAT_LETTERS = {"A", "B", "C", "D", "E", "F"};

    @Override
    public Map<String, Map<String, String>> createSeats(Flights flights) {
        Map<String, Map<String, String>> seatStatuses = new HashMap<>();
        for (int row = 1; row <= NUM_ROWS; row++) {
            for (int seat = 1; seat <= SEATS_PER_ROW; seat++) {
                String seatNumber = SEAT_LETTERS[(row - 1) % SEAT_LETTERS.length] + seat;
                Map<String, String> statusMap = new HashMap<>();
                statusMap.put("status", SeatStatus.AVAILABLE.name());
                statusMap.put("class", SeatClass.FIRST_CLASS.name());
                statusMap.put("userId", "");
                seatStatuses.put(seatNumber, statusMap);
            }
        }
        return seatStatuses;
    }
}
